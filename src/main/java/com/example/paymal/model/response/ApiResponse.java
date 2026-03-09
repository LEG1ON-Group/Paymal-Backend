package com.example.paymal.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private Meta meta;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .meta(Meta.builder().success(true).statusCode(200).build())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, Meta meta) {
        if (meta == null) meta = Meta.builder().build();
        if (meta.getSuccess() == null) meta.setSuccess(true);
        if (meta.getStatusCode() == null) meta.setStatusCode(200);
        return ApiResponse.<T>builder()
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .meta(Meta.builder().success(true).statusCode(200).message(message).build())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .meta(Meta.builder().success(false).statusCode(400).message(message).build())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .meta(Meta.builder().success(false).statusCode(statusCode).message(message).build())
                .build();
    }
}