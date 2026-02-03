package com.spotprice.domain.exception;

import java.math.BigDecimal;

public class PriceMismatchException extends DomainException {

    public PriceMismatchException(BigDecimal expected, BigDecimal actual) {
        super("Price mismatch: expected " + expected + ", actual " + actual);
    }
}
