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
 * Các endpoint công khai được bỏ qua.
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

        // Bỏ qua các endpoint công khai
        if (isOpenEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Kiểm tra Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }

        // Validate JWT token
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return onError(exchange, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        // Token hợp lệ — truyền thông tin user qua header cho downstream services
        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role != null ? role : "")
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        // Chạy trước Logging Filter (order = 1)
        return 0;
    }

    /**
     * Kiểm tra xem path có thuộc danh sách endpoint công khai không.
     */
    private boolean isOpenEndpoint(String path) {
        return openEndpoints.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Trả về lỗi với format thống nhất.
     */
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
