package com.spotprice.domain.order;

import java.util.Objects;

/**
 * 멱등성 키 VO
 */
public record IdempotencyKey(String value) {

    public IdempotencyKey {
        Objects.requireNonNull(value, "IdempotencyKey value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("IdempotencyKey value must not be blank");
        }
    }
}
