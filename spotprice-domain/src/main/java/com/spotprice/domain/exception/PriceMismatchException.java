package com.spotprice.domain.exception;

import java.math.BigDecimal;

public class PriceMismatchException extends DomainException {

    private final BigDecimal serverPrice;

    public PriceMismatchException(BigDecimal clientPrice, BigDecimal serverPrice) {
        super("Price mismatch: client " + clientPrice + ", server " + serverPrice);
        this.serverPrice = serverPrice;
    }

    public BigDecimal getServerPrice() {
        return serverPrice;
    }
}
