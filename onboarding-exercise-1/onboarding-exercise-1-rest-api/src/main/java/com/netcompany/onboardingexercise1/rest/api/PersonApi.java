package com.netcompany.onboardingexercise1.rest.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netcompany.onboardingexercise1.rest.dto.Person;
import com.netcompany.onboardingexercise1.rest.dto.PersonFilterRequest;
import com.netcompany.onboardingexercise1.shared.dto.Create;
import com.netcompany.onboardingexercise1.shared.dto.MessageResponse;
import com.netcompany.onboardingexercise1.shared.dto.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
    ResponseEntity<MessageResponse> add(@Validated(Create.class) @RequestBody Person person)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;

    @GetMapping(value = BASE_URL, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<Person>> find(@Valid @ModelAttribute PersonFilterRequest personFilterRequest);

    @GetMapping(value = BASE_URL + "/{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Person> findById(@PathVariable Long id);

    @GetMapping(value = BASE_URL + "/tax/{taxNumber}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Person> findByTaxNumber(
            @PathVariable @Pattern(regexp = "^[A-Z0-9]+$", message = "Tax number must contain only uppercase letters and digits") @Size(max = 32, message = "Tax number must be less than 33 characters") String taxNumber);

    @PutMapping(value = BASE_URL + "/{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<MessageResponse> update(@Validated(Update.class) @RequestBody Person person, @PathVariable Long id)
            throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException;

    @DeleteMapping(value = BASE_URL + "/{id}")
    ResponseEntity<MessageResponse> delete(@PathVariable Long id) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException;
}
