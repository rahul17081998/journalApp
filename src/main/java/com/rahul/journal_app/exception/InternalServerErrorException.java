package com.rahul.journal_app.exception;

import com.rahul.journal_app.constants.ErrorCode;

public class InternalServerErrorException extends RuntimeException{
    private final ErrorCode errorCode;

    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InternalServerErrorException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
