package com.estebanm.auditing.audit_table;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class AuditEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        private String previousValue;
        private String newValue;
        private String entityName;
        private LocalDateTime operationDate;
        @Column(name = "username")
        private String user;
        private String operation;
}
