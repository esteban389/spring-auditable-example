package com.estebanm.auditing.audit_table;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entityName","entityId","version"})
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuditEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        private String previousValue;
        private String newValue;
        private String entityName;
        private Long entityId;
        private LocalDateTime operationDate;
        @Column(name = "username")
        private String user;
        private Integer entityVersion;
        private String operation;
        private String ipAddress;
        private String userAgent;
        private String pathInfo;

        @PreUpdate
        public void preUpdate() {
                throw new UnsupportedOperationException("Update operation not supported");
        }
}
