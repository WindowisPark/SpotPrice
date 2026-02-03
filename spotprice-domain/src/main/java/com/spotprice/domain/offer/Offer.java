package com.spotprice.domain.offer;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Offer 애그리거트 루트
 * TODO: 필드 및 비즈니스 로직 구현
 */
public class Offer {

    private Long id;
    private OfferStatus status;
    private DecayType decayType;
    private BigDecimal basePrice;
    private Instant expiresAt;

    // TODO: 생성자, 팩토리 메서드, 비즈니스 메서드 구현
}
