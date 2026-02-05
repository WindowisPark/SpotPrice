package com.spotprice.domain.common;

import java.time.Instant;


public interface DomainEvent {

    Instant occurredAt();

    // TODO: 필요한 메서드 추가 고려
    // - String eventType();
    // - Long aggregateId();
}
