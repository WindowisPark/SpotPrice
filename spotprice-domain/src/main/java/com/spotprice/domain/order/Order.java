package com.spotprice.domain.order;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {

    private Long id;
    private Long offerId;
    private OrderStatus status;
    private BigDecimal lockedPrice;
    private IdempotencyKey idempotencyKey;
    private Instant createdAt;

    /**
     * 새 주문 생성 — PENDING 상태로 시작
     */
    public Order(Long offerId, BigDecimal lockedPrice, IdempotencyKey idempotencyKey, Instant createdAt) {
        if (offerId == null) {
            throw new IllegalArgumentException("offerId는 필수입니다.");
        }
        if (lockedPrice == null || lockedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("확정 가격은 0보다 커야 합니다.");
        }
        if (idempotencyKey == null) {
            throw new IllegalArgumentException("멱등성 키는 필수입니다.");
        }

        this.offerId = offerId;
        this.lockedPrice = lockedPrice;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.status = OrderStatus.PENDING;
    }

    /**
     * DB 복원용 팩토리
     */
    public static Order restore(Long id, Long offerId, OrderStatus status,
                                BigDecimal lockedPrice, IdempotencyKey idempotencyKey,
                                Instant createdAt) {
        Order order = new Order();
        order.id = id;
        order.offerId = offerId;
        order.status = status;
        order.lockedPrice = lockedPrice;
        order.idempotencyKey = idempotencyKey;
        order.createdAt = createdAt;
        return order;
    }

    private Order() {
    }

    // --- 상태 전이 ---

    public void markPaid() {
        requirePending();
        this.status = OrderStatus.PAID;
    }

    public void markFailed() {
        requirePending();
        this.status = OrderStatus.FAILED;
    }

    public void cancel() {
        requirePending();
        this.status = OrderStatus.CANCELLED;
    }

    private void requirePending() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "PENDING 상태에서만 변경 가능합니다. 현재 status=" + status);
        }
    }

    // --- Getters ---

    public Long getId() { return id; }
    public Long getOfferId() { return offerId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getLockedPrice() { return lockedPrice; }
    public IdempotencyKey getIdempotencyKey() { return idempotencyKey; }
    public Instant getCreatedAt() { return createdAt; }
}
