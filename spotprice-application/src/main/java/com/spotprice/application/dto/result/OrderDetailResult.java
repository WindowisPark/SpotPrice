package com.spotprice.application.dto.result;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderDetailResult(
        Long orderId,
        Long offerId,
        BigDecimal lockedPrice,
        String status,
        Instant createdAt
) {
}
