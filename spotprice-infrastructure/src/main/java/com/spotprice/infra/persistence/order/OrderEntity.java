package com.spotprice.infra.persistence.order;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long offerId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private BigDecimal lockedPrice;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(Long id, Long offerId, String status,
                       BigDecimal lockedPrice, String idempotencyKey,
                       Instant createdAt) {
        this.id = id;
        this.offerId = offerId;
        this.status = status;
        this.lockedPrice = lockedPrice;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getOfferId() { return offerId; }
    public String getStatus() { return status; }
    public BigDecimal getLockedPrice() { return lockedPrice; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Instant getCreatedAt() { return createdAt; }
}
