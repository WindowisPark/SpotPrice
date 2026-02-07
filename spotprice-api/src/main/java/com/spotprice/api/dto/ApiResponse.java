package com.spotprice.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorDetail error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> error(ErrorCode code) {
        return new ApiResponse<>(false, null, new ErrorDetail(code.name(), code.getMessage()));
    }

    public static ApiResponse<Void> error(ErrorCode code, Map<String, Object> extra) {
        return new ApiResponse<>(false, null, new ErrorDetail(code.name(), code.getMessage(), extra));
    }
}
