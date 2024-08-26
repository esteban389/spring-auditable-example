package com.estebanm.auditing.person;

import com.estebanm.auditing.audit_listeners.AuditEntityState;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/persons")
@Slf4j
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public Iterable<Person> getPersons(HttpServletRequest request) {
        log.info(request.getHeader("User-Agent"));
        return personRepository.findAll();
    }

    @PostMapping
    public Person createPerson(@RequestBody CreatePersonDto person) {
        Person newPerson = Person.builder().email(person.getEmail()).name(person.getName()).build();
        return personRepository.save(newPerson);
    }

    @PutMapping("/{id}")
    public Person updatePerson(@PathVariable Long id, @RequestBody UpdatePersonDto person) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        existingPerson.setEmail(person.getEmail());
        existingPerson.setName(person.getName());
        return personRepository.save(existingPerson);
    }

    @PatchMapping("/{id}")
    public Person patchPerson(@PathVariable Long id, @RequestBody UpdatePersonDto person) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        if (person.getEmail() != null) {
            existingPerson.setEmail(person.getEmail());
        }
        if (person.getName() != null) {
            existingPerson.setName(person.getName());
        }

        return personRepository.save(existingPerson);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Long id) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        existingPerson.setStatus(AuditEntityState.DISABLED);
        personRepository.save(existingPerson);
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        return personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
    }

    @PostMapping("/{id}")
    public Person restorePerson(@PathVariable Long id) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        existingPerson.setStatus(AuditEntityState.ENABLED);
        return personRepository.save(existingPerson);
    }
}
