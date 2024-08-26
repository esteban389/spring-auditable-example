package com.estebanm.auditing;

import com.estebanm.auditing.audit_listeners.AuditEntityState;
import com.estebanm.auditing.audit_listeners.AuditableEntity;
import com.estebanm.auditing.audit_table.AuditEntity;
import com.estebanm.auditing.audit_table.AuditEntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class HibernateEventListeners implements PostUpdateEventListener, PostInsertEventListener {

    private final AuditEntityRepository auditEntityRepository;

    private final ObjectMapper objectMapper;

    public HibernateEventListeners(AuditEntityRepository auditEntityRepository, ObjectMapper objectMapper, EntityManager entityManager) {
        this.auditEntityRepository = auditEntityRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        log.info("Post update event: {}", postUpdateEvent);
        if (postUpdateEvent.getEntity() instanceof AuditableEntity auditableEntity) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication != null ? authentication.getName() : "Guest";
            LocalDateTime now = LocalDateTime.now();
            String operation = "UPDATE";
            HttpServletRequest curRequest =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();
            AuditEntityState state = Arrays.stream(postUpdateEvent.getOldState())
                    .filter(AuditEntityState.class::isInstance)
                    .map(AuditEntityState.class::cast)
                    .findFirst()
                    .orElseThrow();
            boolean isStatusChanged = !Objects.equals(auditableEntity.getStatus(), state);
            if (isStatusChanged) {
                operation = Objects.equals(auditableEntity.getStatus(), AuditEntityState.ENABLED) ? "RESTORE" : "DELETE";
            }

            String[] propertyNames = postUpdateEvent.getPersister().getPropertyNames();

            Map<String, Object> previousValues = new HashMap<>();
            Map<String, Object> newValues = new HashMap<>();
            for (int i = 0; i < propertyNames.length; i++) {
                previousValues.put(propertyNames[i], postUpdateEvent.getOldState()[i]);
                newValues.put(propertyNames[i], postUpdateEvent.getState()[i]);
            }

            try {
                AuditEntity auditEntity = AuditEntity.builder()
                        .entityName(postUpdateEvent.getEntity().getClass().getSimpleName())
                        .operation(operation)
                        .entityId(auditableEntity.getId())
                        .previousValue(objectMapper.writeValueAsString(previousValues))
                        .newValue(objectMapper.writeValueAsString(newValues))
                        .operationDate(now)
                        .user(principal)
                        .entityVersion(Integer.parseInt(newValues.get("version").toString()))
                        .ipAddress(curRequest.getRemoteAddr())
                        .userAgent(curRequest.getHeader("User-Agent"))
                        .pathInfo(curRequest.getRequestURL().toString())
                        .build();
                auditEntity = auditEntityRepository.save(auditEntity);
                log.info("Audit event saved: {}", auditEntity);
            } catch (JsonProcessingException e) {
                log.error("Error processing audit event", e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("Entity is not auditable");
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostInsert(PostInsertEvent postInsertEvent) {

        log.info("Post insert event: {}", postInsertEvent);
        if (postInsertEvent.getEntity() instanceof AuditableEntity auditableEntity) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication != null ? authentication.getName() : "Guest";
            LocalDateTime now = LocalDateTime.now();
            String operation = "INSERT";
            HttpServletRequest curRequest =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();

            String[] propertyNames = postInsertEvent.getPersister().getPropertyNames();

            Map<String, Object> newValues = new HashMap<>();
            for (int i = 0; i < propertyNames.length; i++) {
                newValues.put(propertyNames[i], postInsertEvent.getState()[i]);
            }

            try {
                AuditEntity auditEntity = AuditEntity.builder()
                        .entityName(postInsertEvent.getEntity().getClass().getSimpleName())
                        .operation(operation)
                        .entityId(auditableEntity.getId())
                        .previousValue(objectMapper.writeValueAsString(null))
                        .newValue(objectMapper.writeValueAsString(newValues))
                        .operationDate(now)
                        .user(principal)
                        .entityVersion(Integer.parseInt(newValues.get("version").toString()))
                        .ipAddress(curRequest.getRemoteAddr())
                        .userAgent(curRequest.getHeader("User-Agent"))
                        .pathInfo(curRequest.getRequestURL().toString())
                        .build();
                auditEntity = auditEntityRepository.save(auditEntity);
                log.info("Audit event saved: {}", auditEntity);
            } catch (JsonProcessingException e) {
                log.error("Error processing audit event", e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("Entity is not auditable");
        }
    }
}
