package com.spotprice.domain.exception;

public class AuthenticationFailedException extends DomainException {

    public AuthenticationFailedException() {
        super("Invalid email or password");
    }
}
