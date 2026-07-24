package com.englishlms.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response wrapper thống nhất cho toàn bộ hệ thống.
 * Tất cả API trả về định dạng: { success, message, data }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
