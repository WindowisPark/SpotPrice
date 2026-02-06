package com.spotprice.application.dto.event;

import java.time.Instant;

public record OrderPaidEvent(
        Long orderId,
        Instant occurredAt
) {
}
