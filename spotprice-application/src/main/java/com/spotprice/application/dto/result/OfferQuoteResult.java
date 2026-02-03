package com.spotprice.application.dto.result;

import java.math.BigDecimal;
import java.time.Instant;

public record OfferQuoteResult(
        Long offerId,
        BigDecimal currentPrice,
        Instant quotedAt,
        Instant expiresAt
) {
}
