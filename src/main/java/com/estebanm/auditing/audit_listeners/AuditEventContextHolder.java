package com.estebanm.auditing.audit_listeners;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditEventContextHolder<T> {
    private T entity;
    private AuditEventType eventType;
    private String principal;
    private String details;
    private LocalDateTime operationDate;
    private T previousEntity;

}
