package com.rahul.journal_app.exception;

import com.rahul.journal_app.constants.ErrorCode;

public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
