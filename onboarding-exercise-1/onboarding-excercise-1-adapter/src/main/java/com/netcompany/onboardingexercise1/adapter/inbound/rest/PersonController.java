package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netcompany.onboardingexercise1.shared.annotation.audit.Audit;
import com.netcompany.onboardingexercise1.adapter.mapper.PersonFilterMapper;
import com.netcompany.onboardingexercise1.adapter.mapper.PersonMapper;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.service.person.PersonEventService;
import com.netcompany.onboardingexercise1.core.service.person.PersonService;
import com.netcompany.onboardingexercise1.rest.api.PersonApi;
import com.netcompany.onboardingexercise1.rest.dto.Person;
import com.netcompany.onboardingexercise1.rest.dto.PersonFilterRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Validated
@Slf4j
public class PersonController implements PersonApi {

    private final PersonService personService;

    private final PersonEventService personEventService;

    private final PersonMapper personMapper;

    private final PersonFilterMapper personFilterMapper;


    public PersonController(PersonService personService, PersonEventService personEventService, PersonMapper personMapper,
            PersonFilterMapper personFilterMapper) {
        this.personService = personService;
        this.personEventService = personEventService;
        this.personMapper = personMapper;
        this.personFilterMapper = personFilterMapper;
    }

    @Override
    @Audit(action = "Add People via REST")
    public ResponseEntity<String> add(Person person) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        personEventService.create(personMapper.dtoToDomain(person));

        return ResponseEntity.status(HttpStatus.CREATED).body("Person created successfully");
    }

    @Override
    @Audit(action = "Find People via REST")
    public ResponseEntity<Page<Person>> find(PersonFilterRequest personFilterRequest) {
        Page<PersonDomain> personDomainPage = personService.find(personFilterMapper.toPersonFilter(personFilterRequest));

        Page<Person> personPage = personDomainPage.map(personMapper::domainToDto);
        return ResponseEntity.ok(personPage);
    }

    @Override
    @Audit(action = "Find Person by ID via REST")
    public ResponseEntity<Person> findById(Long id) {
        PersonDomain personDomain = personService.findById(id);
        Person person = personMapper.domainToDto(personDomain);
        return ResponseEntity.ok(person);
    }

    @Override
    @Audit(action = "Find Person by Tax Number via REST")
    public ResponseEntity<Person> findByTaxNumber(String taxNumber) {
        PersonDomain personDomain = personService.findByTaxNumber(taxNumber);
        Person person = personMapper.domainToDto(personDomain);
        return ResponseEntity.ok(person);
    }

    @Override
    @Audit(action = "Update Person via REST")
    public ResponseEntity<String> update(Person person, Long id) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        personEventService.update(id, personMapper.dtoToDomain(person));
        return ResponseEntity.status(HttpStatus.OK).body("Person updated successfully");
    }

    @Override
    @Audit(action = "Delete Person via REST")
    public ResponseEntity<Void> delete(Long id) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        personEventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
