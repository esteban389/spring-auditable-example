package com.estebanm.auditing.audit_listeners;

import org.springframework.context.ApplicationEvent;

public class AuditEvent extends ApplicationEvent {

    public AuditEvent(Object source) {
        super(source);
        if(!(source instanceof AuditEventContextHolder)){
            throw new IllegalArgumentException("Source must be an instance of AuditEventContextHolder");
        }
    }

    public AuditEventContextHolder<?> getAuditEventContextHolder() {
        return (AuditEventContextHolder<?>) getSource();
    }
}
