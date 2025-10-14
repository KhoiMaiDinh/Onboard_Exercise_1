package com.netcompany.onboardingexercise1.core.port.outbound.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netcompany.onboardingexercise1.core.domain.event.PersonEventDomain;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface PersonEventPort {
    void produce(PersonEventDomain personEventDomain) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;
}
