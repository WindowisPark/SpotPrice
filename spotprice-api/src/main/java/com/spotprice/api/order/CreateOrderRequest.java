package com.spotprice.api.order;

import java.math.BigDecimal;

public record CreateOrderRequest(
        Long offerId,
        BigDecimal expectedPrice,
        String idempotencyKey
) {
}
