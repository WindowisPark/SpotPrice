package com.spotprice.domain.order;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Order 애그리거트 루트
 * TODO: 필드 및 비즈니스 로직 구현
 */
public class Order {

    private Long id;
    private Long offerId;
    private OrderStatus status;
    private BigDecimal lockedPrice;
    private IdempotencyKey idempotencyKey;
    private Instant createdAt;

    // TODO: 생성자, 팩토리 메서드, 상태 전이 메서드 구현
}
