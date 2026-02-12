package com.spotprice.infra.persistence.audit;

import com.spotprice.application.dto.AuditEventType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_event_type", columnList = "eventType"),
        @Index(name = "idx_audit_logs_aggregate", columnList = "aggregateType, aggregateId")
})
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuditEventType eventType;

    private Long userId;

    @Column(nullable = false, length = 30)
    private String aggregateType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private Instant occurredAt;

    protected AuditLogEntity() {
    }

    public AuditLogEntity(AuditEventType eventType, Long userId,
                          String aggregateType, Long aggregateId,
                          String detail, Instant occurredAt) {
        this.eventType = eventType;
        this.userId = userId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.detail = detail;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public AuditEventType getEventType() { return eventType; }
    public Long getUserId() { return userId; }
    public String getAggregateType() { return aggregateType; }
    public Long getAggregateId() { return aggregateId; }
    public String getDetail() { return detail; }
    public Instant getOccurredAt() { return occurredAt; }
}
