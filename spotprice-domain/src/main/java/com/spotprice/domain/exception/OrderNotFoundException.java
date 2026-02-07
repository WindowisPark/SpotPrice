package com.spotprice.domain.exception;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
    }
}
