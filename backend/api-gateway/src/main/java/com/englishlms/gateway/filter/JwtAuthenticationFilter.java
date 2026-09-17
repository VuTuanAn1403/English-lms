package com.englishlms.gateway.filter;

import com.englishlms.gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT Authentication Filter cho Gateway.
 * Kiểm tra JWT token trên các request cần xác thực.
 * Các endpoint công khai được bổ sung header nếu có JWT token đính kèm.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final List<String> openEndpoints;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            @Value("${gateway.open-endpoints}") List<String> openEndpoints
    ) {
        this.jwtUtil = jwtUtil;
        this.openEndpoints = openEndpoints;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "";

        // 1. Xóa các header định danh do client tự gửi để tránh giả mạo (Header Spoofing)
        ServerHttpRequest sanitizedRequest = request.mutate()
                .headers(headers -> {
                    headers.remove("X-User-Email");
                    headers.remove("X-User-Role");
                    headers.remove("X-User-Id");
                })
                .build();

        String authHeader = sanitizedRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 2. Kiểm tra endpoint công khai (xem xét cả HTTP method)
        if (isOpenEndpoint(path, method)) {
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());
                if (jwtUtil.validateToken(token)) {
                    ServerHttpRequest authenticatedRequest = attachUserHeaders(sanitizedRequest, token);
                    return chain.filter(exchange.mutate().request(authenticatedRequest).build());
                }
            }
            return chain.filter(exchange.mutate().request(sanitizedRequest).build());
        }

        // 3. Endpoint yêu cầu xác thực JWT
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {} [{}]", path, method);
            return onError(exchange, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token for path: {} [{}]", path, method);
            return onError(exchange, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        ServerHttpRequest authenticatedRequest = attachUserHeaders(sanitizedRequest, token);
        return chain.filter(exchange.mutate().request(authenticatedRequest).build());
    }

    private ServerHttpRequest attachUserHeaders(ServerHttpRequest request, String token) {
        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        String userId = jwtUtil.getUserIdFromToken(token);

        ServerHttpRequest.Builder builder = request.mutate()
                .header("X-User-Email", email != null ? email : "")
                .header("X-User-Role", role != null ? role : "");

        if (userId != null && !userId.isBlank()) {
            builder.header("X-User-Id", userId);
        }

        return builder.build();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isOpenEndpoint(String path, String method) {
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Auth endpoints
        if (pathMatcher.match("/api/v1/auth/**", path) ||
            pathMatcher.match("/user/auth/**", path) ||
            pathMatcher.match("/user/login", path) ||
            pathMatcher.match("/user/register", path) ||
            pathMatcher.match("/swagger-ui/**", path) ||
            pathMatcher.match("/swagger-ui.html", path) ||
            pathMatcher.match("/v3/api-docs/**", path) ||
            pathMatcher.match("/eureka/**", path) ||
            pathMatcher.match("/actuator/**", path)) {
            return true;
        }

        // Public course & lesson catalog (chỉ cho phép GET method)
        if ("GET".equalsIgnoreCase(method)) {
            if (pathMatcher.match("/api/v1/courses", path) ||
                pathMatcher.match("/api/v1/courses/*", path) ||
                pathMatcher.match("/api/v1/courses/*/lessons", path) ||
                pathMatcher.match("/api/v1/courses/*/enrolled-count", path) ||
                pathMatcher.match("/api/v1/lessons", path) ||
                pathMatcher.match("/api/v1/lessons/*", path) ||
                pathMatcher.match("/courses/**", path) ||
                pathMatcher.match("/lessons/**", path) ||
                pathMatcher.match("/course/public/**", path)) {
                return true;
            }
        }

        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"message\":\"%s\",\"data\":null}",
                message
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(bytes))
        );
    }
}
