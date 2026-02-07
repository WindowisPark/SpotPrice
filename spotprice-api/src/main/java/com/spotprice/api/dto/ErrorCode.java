package com.spotprice.api.dto;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    OFFER_NOT_FOUND(HttpStatus.NOT_FOUND, "Offer not found"),
    OFFER_EXPIRED(HttpStatus.GONE, "Offer has expired"),
    OFFER_ALREADY_SOLD(HttpStatus.CONFLICT, "Offer has already been sold"),
    PRICE_INCREASED(HttpStatus.CONFLICT, "Price has increased since quote"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "Invalid order status"),
    PAYMENT_CONFLICT(HttpStatus.CONFLICT, "Another user completed the payment");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
