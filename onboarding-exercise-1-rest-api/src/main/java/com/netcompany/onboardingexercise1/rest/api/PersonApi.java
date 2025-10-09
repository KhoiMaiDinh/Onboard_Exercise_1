package com.netcompany.onboardingexercise1.rest.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.netcompany.onboardingexercise1.rest.common.dto.Create;
import com.netcompany.onboardingexercise1.rest.common.dto.Update;
import com.netcompany.onboardingexercise1.rest.dto.Person;
import com.netcompany.onboardingexercise1.rest.dto.PersonFilterRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
public interface PersonApi {

    String BASE_URL = "/people";

    @PostMapping(value = BASE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Person> add(@Validated(Create.class) @RequestBody Person personDto);

    @GetMapping(value = BASE_URL, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<Person>> find(@Valid @ModelAttribute PersonFilterRequest personFilterRequest);

    @GetMapping(value = BASE_URL + "/{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Person> findById(@PathVariable Long id);

    @PutMapping(value = BASE_URL + "/{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Person> update(@Validated(Update.class) @RequestBody Person person, @PathVariable Long id);

    @DeleteMapping(value = BASE_URL + "/{id}")
    ResponseEntity<Void> delete(@PathVariable long id);
}
