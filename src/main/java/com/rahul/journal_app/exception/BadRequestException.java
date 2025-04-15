package com.rahul.journal_app.exception;

import com.rahul.journal_app.constants.ErrorCode;

public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
} 