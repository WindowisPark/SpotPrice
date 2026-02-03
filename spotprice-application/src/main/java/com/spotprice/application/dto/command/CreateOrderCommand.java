package com.spotprice.application.dto.command;

import java.math.BigDecimal;

public record CreateOrderCommand(
        Long offerId,
        BigDecimal expectedPrice,
        String idempotencyKey
) {
}
