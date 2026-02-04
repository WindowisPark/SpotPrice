package com.spotprice.domain.offer;

import com.spotprice.domain.common.DomainEvent;

import java.time.Instant;

/**
 * Offer 판매 완료 이벤트
 *
 * - Offer Aggregate가 SOLD 상태로 전이될 때 발생
 * - 이미 발생한 사실을 표현하는 불변 이벤트
 */
public record OfferSoldEvent(
        Long offerId,
        Instant occurredAt
) implements DomainEvent {
}
