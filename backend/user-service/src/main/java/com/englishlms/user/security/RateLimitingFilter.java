package com.englishlms.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static class RequestCount {
        final long timestamp;
        final AtomicInteger count;

        RequestCount(long timestamp) {
            this.timestamp = timestamp;
            this.count = new AtomicInteger(1);
        }
    }

    private final Map<String, RequestCount> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_LOGIN_REQUESTS_PER_MINUTE = 15;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.endsWith("/login")) {
            String clientIp = getClientIp(request);
            long now = System.currentTimeMillis();

            RequestCount reqCount = requestCounts.compute(clientIp, (key, existing) -> {
                if (existing == null || now - existing.timestamp > 60000) {
                    return new RequestCount(now);
                }
                existing.count.incrementAndGet();
                return existing;
            });

            if (reqCount.count.get() > MAX_LOGIN_REQUESTS_PER_MINUTE) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                String body = "{\"success\":false,\"message\":\"Quá nhiều yêu cầu đăng nhập. Vui lòng thử lại sau 1 phút.\",\"data\":null}";
                response.getWriter().write(body);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
