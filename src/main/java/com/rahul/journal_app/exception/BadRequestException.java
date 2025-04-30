package com.rahul.journal_app.exception;

import com.rahul.journal_app.constants.ErrorCode;

public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    public BadRequestException(String message){
        super(message);
        this.errorCode=null;
    }
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
} 