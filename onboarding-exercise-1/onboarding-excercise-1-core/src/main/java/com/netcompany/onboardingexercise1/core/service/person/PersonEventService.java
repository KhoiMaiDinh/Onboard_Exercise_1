package com.netcompany.onboardingexercise1.core.service.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface PersonEventService {
    void create(PersonDomain person) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;
    void update(Long id, PersonDomain person) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;
}
