package com.estebanm.auditing.person;

import com.estebanm.auditing.audit_listeners.AuditEventPublisher;
import com.estebanm.auditing.audit_listeners.AuditEventType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/persons")
@Slf4j
public class PersonController {

    private final PersonRepository personRepository;

    private final AuditEventPublisher eventPublisher;
    public PersonController(PersonRepository personRepository, AuditEventPublisher eventPublisher) {
        this.personRepository = personRepository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public Iterable<Person> getPersons(HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining(log::info);
        log.info(request.getPathInfo());
        log.info(request.getRequestURI());
        log.info(request.getRequestURL().toString());
        log.info(request.getContextPath());
        log.info(request.getServletPath());
        log.info(request.getRemoteAddr());
        log.info(request.getRemoteHost());
        log.info(request.getRemoteUser());
        log.info(request.getHeader("User-Agent"));
        return personRepository.findAll();
    }

    @PostMapping
    public Person createPerson(@RequestBody CreatePersonDto person) {
        Person newPerson = Person.builder().email(person.getEmail()).name(person.getName()).build();
        eventPublisher.publishAuditEvent(newPerson);
        return personRepository.save(newPerson);
    }

    @PutMapping("/{id}")
    public Person updatePerson(@PathVariable Long id, @RequestBody UpdatePersonDto person) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        Person updatedPerson = existingPerson.toBuilder().email(person.getEmail()).name(person.getName()).build();
        eventPublisher.publishAuditEvent(existingPerson, updatedPerson, AuditEventType.UPDATE);
        return personRepository.save(updatedPerson);
    }

    @PatchMapping("/{id}")
    public Person patchPerson(@PathVariable Long id, @RequestBody UpdatePersonDto person) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        var updatedPersonBuilder = existingPerson.toBuilder();
        if (person.getEmail() != null) {
            updatedPersonBuilder.email(person.getEmail());
        }
        if (person.getName() != null) {
            updatedPersonBuilder.name(person.getName());
        }

        Person updatedPerson = updatedPersonBuilder.build();
        eventPublisher.publishAuditEvent(existingPerson, updatedPerson, AuditEventType.UPDATE);
        return personRepository.save(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Long id) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        Person deletedPerson = existingPerson.toBuilder().build();
        deletedPerson.setStatus("DELETED");
        eventPublisher.publishAuditEvent(existingPerson, deletedPerson, AuditEventType.DELETE);
        personRepository.save(deletedPerson);
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        return personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
    }

    @PostMapping("/{id}")
    public Person restorePerson(@PathVariable Long id) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        Person restoredPerson = existingPerson.toBuilder().build();
        restoredPerson.setStatus("ACTIVE");
        eventPublisher.publishAuditEvent(existingPerson, restoredPerson, AuditEventType.RESTORE);
        return personRepository.save(restoredPerson);
    }
}
