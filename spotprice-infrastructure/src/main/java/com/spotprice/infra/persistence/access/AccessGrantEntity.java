package com.spotprice.infra.persistence.access;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "access_grants")
public class AccessGrantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private String grantType;

    @Column(nullable = false)
    private String grantValue;

    @Column(nullable = false)
    private Instant validFrom;

    @Column(nullable = false)
    private Instant validTo;

    @Column(nullable = false)
    private String status;

    protected AccessGrantEntity() {
    }

    public AccessGrantEntity(Long id, Long orderId, String grantType, String grantValue,
                             Instant validFrom, Instant validTo, String status) {
        this.id = id;
        this.orderId = orderId;
        this.grantType = grantType;
        this.grantValue = grantValue;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getGrantType() { return grantType; }
    public String getGrantValue() { return grantValue; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public String getStatus() { return status; }
}
