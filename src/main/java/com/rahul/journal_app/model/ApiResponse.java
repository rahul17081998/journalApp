package com.rahul.journal_app.model;

import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.enums.StatusEnum;
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

    private StatusEnum status;
    private String message;
    @Builder.Default
    private int statusCode = HttpStatus.OK.value();
    private T data;
    @Builder.Default
    private ErrorDetails error=null;
//    @Builder.Default
//    private String statusName = HttpStatus.OK.name();
//    private LocalDateTime timestamp;


    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(StatusEnum.SUCCESSFUL)
                .message(message)
                .data(data)
                //.error(null)
                .statusCode(HttpStatus.OK.value())
                //.statusName(HttpStatus.OK.name())
                //.timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .status(StatusEnum.SUCCESSFUL)
                .message(message)
                .data(data)
                //.error(null)
                .statusCode(status.value())
                //.statusName(status.name())
                //.timestamp(LocalDateTime.now())
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
                .status(StatusEnum.FAILED)
                .message(message)
                .error(errorDetails)
                .statusCode(status.value())
                //.statusName(status.name())
                //.timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String details, HttpStatus status) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(errorCode.getCode())
                .details(details)
                .build();

        return ApiResponse.<T>builder()
                .status(StatusEnum.FAILED)
                .message(errorCode.getMessage())
                .error(errorDetails)
                .statusCode(status.value())
                //.statusName(status.name())
                //.timestamp(LocalDateTime.now())
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