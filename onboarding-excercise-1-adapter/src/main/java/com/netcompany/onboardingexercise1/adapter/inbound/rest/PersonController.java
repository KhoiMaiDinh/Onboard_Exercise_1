package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.netcompany.onboardingexercise1.adapter.mapper.PersonFilterMapper;
import com.netcompany.onboardingexercise1.adapter.mapper.PersonMapper;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.service.person.PersonService;
import com.netcompany.onboardingexercise1.rest.api.PersonApi;
import com.netcompany.onboardingexercise1.rest.dto.Person;
import com.netcompany.onboardingexercise1.rest.dto.PersonFilterRequest;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1")
@Validated
public class PersonController implements PersonApi {

    private final PersonService personService;

    private final PersonMapper personMapper;

    private final PersonFilterMapper personFilterMapper;

    public PersonController(PersonService personService, PersonMapper personMapper, PersonFilterMapper personFilterMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
        this.personFilterMapper = personFilterMapper;
    }

    @Override
    public ResponseEntity<Person> add(Person person) {
        PersonDomain personDomainSaved;

        personDomainSaved = personService.save(personMapper.dtoToDomain(person));

        Person personSaved = personMapper.domainToDto(personDomainSaved);

        URI location = getLocation(personDomainSaved.getId().toString());
        return ResponseEntity.created(location).body(personSaved);
    }

    @Override
    public ResponseEntity<Page<Person>> find(PersonFilterRequest personFilterRequest) {
        Page<PersonDomain> personDomainPage = personService.find(personFilterMapper.toPersonFilter(personFilterRequest));

        Page<Person> personPage = personDomainPage.map(personMapper::domainToDto);
        return ResponseEntity.ok(personPage);
    }

    @Override
    public ResponseEntity<Person> findById(Long id) {
        PersonDomain personDomain = personService.findById(id);
        Person person = personMapper.domainToDto(personDomain);
        return ResponseEntity.ok(person);
    }

    @Override
    public ResponseEntity<Person> update(Person person, Long id) {
        PersonDomain personDomainUpdated = personService.update(id, personMapper.dtoToDomain(person));

        Person personUpdated = personMapper.domainToDto(personDomainUpdated);

        return ResponseEntity.ok(personUpdated);
    }

    @Override
    public ResponseEntity<Void> delete(long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }


    private URI getLocation(String id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
