package com.socialplatform.core.application.exception;

public class UsernameAlreadyExistsException extends DuplicateResourceException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
