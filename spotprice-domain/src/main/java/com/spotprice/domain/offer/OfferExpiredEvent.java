package com.spotprice.domain.offer;

import com.spotprice.domain.common.DomainEvent;

import java.time.Instant;

/**
 * Offer 만료 이벤트
 * - Offer Aggregate가 EXPIRED 상태로 전이될 때 발생
 * - 이미 발생한 사실을 표현하는 불변 이벤트(record)
 */
public record OfferExpiredEvent(
        Long offerId,
        Instant occurredAt
) implements DomainEvent {
}
