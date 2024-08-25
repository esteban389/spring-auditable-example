package com.estebanm.auditing.audit_table;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEntityRepository extends JpaRepository<AuditEntity,Long> {
}
