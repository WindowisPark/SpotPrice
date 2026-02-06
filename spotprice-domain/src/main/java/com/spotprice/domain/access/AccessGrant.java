package com.spotprice.domain.access;

import java.time.Instant;

/**
 * AccessGrant 애그리거트 루트
 * - 결제 완료 후 구매자에게 발급되는 접근 권한
 * - Order당 1개 (order_id UNIQUE)
 */
public class AccessGrant {

    private Long id;
    private Long orderId;
    private GrantType grantType;
    private String grantValue;
    private Instant validFrom;
    private Instant validTo;
    private AccessGrantStatus status;

    /**
     * 새 AccessGrant 생성 — ACTIVE 상태로 시작
     */
    public AccessGrant(Long orderId, GrantType grantType, String grantValue,
                       Instant validFrom, Instant validTo) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId는 필수입니다.");
        }
        if (grantType == null) {
            throw new IllegalArgumentException("grantType은 필수입니다.");
        }
        if (grantValue == null || grantValue.isBlank()) {
            throw new IllegalArgumentException("grantValue는 필수입니다.");
        }
        if (validFrom == null || validTo == null) {
            throw new IllegalArgumentException("validFrom, validTo는 필수입니다.");
        }
        if (!validFrom.isBefore(validTo)) {
            throw new IllegalArgumentException("validFrom은 validTo보다 빨라야 합니다.");
        }

        this.orderId = orderId;
        this.grantType = grantType;
        this.grantValue = grantValue;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = AccessGrantStatus.ACTIVE;
    }

    /**
     * DB 복원용 팩토리
     */
    public static AccessGrant restore(Long id, Long orderId, GrantType grantType,
                                       String grantValue, Instant validFrom, Instant validTo,
                                       AccessGrantStatus status) {
        AccessGrant grant = new AccessGrant();
        grant.id = id;
        grant.orderId = orderId;
        grant.grantType = grantType;
        grant.grantValue = grantValue;
        grant.validFrom = validFrom;
        grant.validTo = validTo;
        grant.status = status;
        return grant;
    }

    private AccessGrant() {
    }

    // --- 상태 전이 ---

    public void revoke() {
        if (status != AccessGrantStatus.ACTIVE) {
            throw new IllegalStateException(
                    "ACTIVE 상태에서만 취소 가능합니다. 현재 status=" + status);
        }
        this.status = AccessGrantStatus.REVOKED;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public GrantType getGrantType() { return grantType; }
    public String getGrantValue() { return grantValue; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public AccessGrantStatus getStatus() { return status; }
}
