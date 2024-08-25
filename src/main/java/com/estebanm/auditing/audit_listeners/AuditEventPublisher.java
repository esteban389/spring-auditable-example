package com.estebanm.auditing.audit_listeners;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public AuditEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public <T> void publishAuditEvent(T entity, AuditEventType eventType, String details) {
        publishAuditEvent(null, entity, eventType, details);
    }
    public <T> void publishAuditEvent(T entity) {
        publishAuditEvent(null, entity, AuditEventType.CREATE, "");
    }
    public <T> void publishAuditEvent(T previousEntity, T entity, AuditEventType eventType) {
        publishAuditEvent(previousEntity, entity, eventType, "");
    }
    public <T> void publishAuditEvent(T previousEntity, T entity, AuditEventType eventType, String details) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        AuditEventContextHolder<T> auditEventContextHolder = AuditEventContextHolder.<T>builder()
                .entity(entity)
                .eventType(eventType)
                .principal(principal)
                .operationDate(LocalDateTime.now())
                .previousEntity(previousEntity)
                .details(details)
                .build();
        AuditEvent auditEvent = new AuditEvent(auditEventContextHolder);
        applicationEventPublisher.publishEvent(auditEvent);
    }
}
