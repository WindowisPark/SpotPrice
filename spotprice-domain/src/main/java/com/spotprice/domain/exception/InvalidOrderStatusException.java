package com.spotprice.domain.exception;

public class InvalidOrderStatusException extends DomainException {

    public InvalidOrderStatusException(String currentStatus) {
        super("Invalid order status: " + currentStatus);
    }
}
