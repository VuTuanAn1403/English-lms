package com.englishlms.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Global Exception Handler cho Gateway.
 * Bắt tất cả exception chưa được xử lý và trả về format thống nhất:
 * { "success": false, "message": "...", "data": null }
 */
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Nếu response đã commit, không xử lý được nữa
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        String message;

        if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = switch (status) {
                case NOT_FOUND -> "Không tìm thấy dịch vụ";
                case SERVICE_UNAVAILABLE -> "Dịch vụ tạm thời không khả dụng";
                case GATEWAY_TIMEOUT -> "Dịch vụ phản hồi quá chậm";
                default -> rse.getReason() != null ? rse.getReason() : "Lỗi hệ thống";
            };
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Lỗi hệ thống";
        }

        log.error("Gateway error: {} - {}", status.value(), ex.getMessage());

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "success", false,
                "message", message,
                "data", ""
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorBody);
        } catch (JsonProcessingException e) {
            bytes = ("{\"success\":false,\"message\":\"Lỗi hệ thống\",\"data\":null}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(bytes))
        );
    }
}
