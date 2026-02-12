package com.spotprice.application.dto;

import java.time.Instant;
import java.util.Map;

public record AuditEvent(
        AuditEventType eventType,
        Long userId,
        String aggregateType,
        Long aggregateId,
        Map<String, Object> detail,
        Instant occurredAt
) {
}
