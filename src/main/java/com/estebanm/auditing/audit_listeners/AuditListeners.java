package com.estebanm.auditing.audit_listeners;

import com.estebanm.auditing.audit_table.AuditEntity;
import com.estebanm.auditing.audit_table.AuditEntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AuditListeners {

    private final ObjectMapper objectMapper;

    private final AuditEntityRepository auditEntityRepository;

    public AuditListeners(ObjectMapper objectMapper, AuditEntityRepository auditEntityRepository) {
        this.objectMapper = objectMapper;
        this.auditEntityRepository = auditEntityRepository;
    }

    @Async
    @EventListener
    public void onAuditEvent(AuditEvent auditEvent) {
        log.info("Captured an Audit event");
        try {
            AuditEventContextHolder<?> auditEventContextHolder = auditEvent.getAuditEventContextHolder();
            AuditEntity auditEntity = AuditEntity.builder()
                    .entityName(auditEventContextHolder.getEntity().getClass().getSimpleName())
                    .operation(auditEventContextHolder.getEventType().toString())
                    .previousValue(objectMapper.writeValueAsString(auditEventContextHolder.getPreviousEntity()))
                    .newValue(objectMapper.writeValueAsString(auditEventContextHolder.getEntity()))
                    .operationDate(LocalDateTime.now())
                    .user(auditEventContextHolder.getPrincipal())
                    .build();

            auditEntityRepository.save(auditEntity);
            log.info("Audit entity: {}", auditEntity);
        } catch (JsonProcessingException e) {
            log.error("Error processing audit event", e);
        }
    }

}
