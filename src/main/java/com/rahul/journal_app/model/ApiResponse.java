package com.rahul.journal_app.model;

import com.rahul.journal_app.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    @Builder.Default
    private ErrorDetails error = null;

    @Builder.Default
    private int statusCode = HttpStatus.OK.value();

    @Builder.Default
    private String statusName = HttpStatus.OK.name();
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(HttpStatus.OK.value())
                .statusName(HttpStatus.OK.name())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .error(null)
                .statusCode(status.value())
                .statusName(status.name())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    public static <T> ApiResponse<T> success(String message, HttpStatus status) {
        return success(null, message, status);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, String details) {
        return error(message, errorCode, details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return error(message, errorCode, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return error(message, null, null, status);
    }
    public static <T> ApiResponse<T> error(String message, String errorCode, String details, HttpStatus status) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(errorCode)
                .details(details)
                .build();
                
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .error(errorDetails)
                .statusCode(status.value())
                .statusName(status.name())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String details, HttpStatus status) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(errorCode.getCode())
                .details(details)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .error(errorDetails)
                .statusCode(status.value())
                .statusName(status.name())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, HttpStatus status) {
        return error(errorCode, null, status);
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String details;
    }
} 