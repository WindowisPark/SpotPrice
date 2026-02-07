package com.spotprice.application.dto.result;

import java.math.BigDecimal;
import java.time.Instant;

public record OfferSummaryResult(
        Long offerId,
        BigDecimal basePrice,
        BigDecimal currentPrice,
        BigDecimal minPrice,
        Instant startAt,
        Instant endAt,
        Instant expireAt,
        String status
) {
}
