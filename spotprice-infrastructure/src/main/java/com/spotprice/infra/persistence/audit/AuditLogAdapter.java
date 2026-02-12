package com.spotprice.infra.persistence.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotprice.application.dto.AuditEvent;
import com.spotprice.application.port.out.AuditLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class AuditLogAdapter implements AuditLogPort {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAdapter.class);

    private final JpaAuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditLogAdapter(JpaAuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(AuditEvent event) {
        try {
            AuditLogEntity entity = new AuditLogEntity(
                    event.eventType(),
                    event.userId(),
                    event.aggregateType(),
                    event.aggregateId(),
                    serializeDetail(event.detail()),
                    event.occurredAt()
            );
            repository.save(entity);
        } catch (Exception e) {
            log.warn("감사 로그 저장 실패: eventType={}, aggregateId={}",
                    event.eventType(), event.aggregateId(), e);
        }
    }

    private String serializeDetail(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 detail 직렬화 실패", e);
            return "{}";
        }
    }
}
