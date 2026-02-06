package com.spotprice.application.dto.result;

import java.time.Instant;

public record AccessGrantResult(
        Long orderId,
        String grantType,
        String grantValue,
        Instant validFrom,
        Instant validTo,
        String status
) {
}
