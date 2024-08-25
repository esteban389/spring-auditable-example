package com.estebanm.auditing.person;

import com.estebanm.auditing.audit_listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@With
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Person extends AuditableEntity {

    private String name;
    private String email;
}
