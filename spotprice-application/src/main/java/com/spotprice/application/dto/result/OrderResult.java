package com.spotprice.application.dto.result;

import java.math.BigDecimal;

public record OrderResult(
        Long orderId,
        Long offerId,
        BigDecimal lockedPrice,
        String status
) {
}
