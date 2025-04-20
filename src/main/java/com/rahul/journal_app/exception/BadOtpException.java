package com.rahul.journal_app.exception;

import com.rahul.journal_app.constants.ErrorCode;

public class BadOtpException extends RuntimeException{

    private final ErrorCode errorCode;
    public BadOtpException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
