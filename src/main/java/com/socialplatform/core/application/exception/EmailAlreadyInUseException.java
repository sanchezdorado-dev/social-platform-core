package com.socialplatform.core.application.exception;

public class EmailAlreadyInUseException extends DuplicateResourceException {

    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
