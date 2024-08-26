package com.estebanm.auditing.audit_table;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEntityRepository extends JpaRepository<AuditEntity, UUID> {
}
