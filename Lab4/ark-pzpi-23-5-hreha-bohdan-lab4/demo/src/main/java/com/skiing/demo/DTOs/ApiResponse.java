package com.skiing.demo.DTOs;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    public static ApiResponse<Void> Success() {
        return new ApiResponse<Void>(true, "success", null);
    }

    public static <T> ApiResponse<T> Success(T data) {
        return new ApiResponse<>(true, "success", data);
    }

    public static <T> ApiResponse<T> Success(String message, T data ) {
        return new ApiResponse<>(true, message, data);
    }
}
