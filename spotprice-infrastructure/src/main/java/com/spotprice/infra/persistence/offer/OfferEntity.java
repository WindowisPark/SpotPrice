package com.spotprice.infra.persistence.offer;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "offers")
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String decayType;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private BigDecimal minPrice;

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    @Column(nullable = false)
    private Instant expireAt;

    protected OfferEntity() {
    }

    public OfferEntity(Long id, String status, String decayType,
                       BigDecimal basePrice, BigDecimal minPrice,
                       Instant startAt, Instant endAt, Instant expireAt) {
        this.id = id;
        this.status = status;
        this.decayType = decayType;
        this.basePrice = basePrice;
        this.minPrice = minPrice;
        this.startAt = startAt;
        this.endAt = endAt;
        this.expireAt = expireAt;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getDecayType() { return decayType; }
    public BigDecimal getBasePrice() { return basePrice; }
    public BigDecimal getMinPrice() { return minPrice; }
    public Instant getStartAt() { return startAt; }
    public Instant getEndAt() { return endAt; }
    public Instant getExpireAt() { return expireAt; }
}
