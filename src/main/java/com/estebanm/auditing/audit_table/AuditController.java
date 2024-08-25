package com.estebanm.auditing.audit_table;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditEntityRepository auditRepository;

    public AuditController(AuditEntityRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @GetMapping
    public Iterable<AuditEntity> getAudits() {
        return auditRepository.findAll();
    }
}
