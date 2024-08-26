package com.estebanm.auditing;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Service
public class HibernateEventListenersConfigurer {

    private final HibernateEventListeners hibernateEventListeners;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public HibernateEventListenersConfigurer(HibernateEventListeners hibernateEventListeners, EntityManagerFactory entityManagerFactory) {
        this.hibernateEventListeners = hibernateEventListeners;
    }

    @PostConstruct
    public void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        assert registry != null;
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(hibernateEventListeners);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(hibernateEventListeners);
    }
}
