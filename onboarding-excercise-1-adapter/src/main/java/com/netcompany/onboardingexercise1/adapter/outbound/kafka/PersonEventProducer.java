package com.netcompany.onboardingexercise1.adapter.outbound.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.core.domain.event.PersonEventDomain;
import com.netcompany.onboardingexercise1.core.port.outbound.kafka.PersonEventPort;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonEventProducer implements PersonEventPort {

    @Value("${onboarding-exercise-1.kafka.inbound.person-topic}")
    public String topic;

    KafkaTemplate<Integer, String> kafkaTemplate;

    ObjectMapper objectMapper;

    PersonEventMapper personEventMapper;


    public PersonEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper, PersonEventMapper personEventMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.personEventMapper = personEventMapper;
    }

    @Audit(action = "Person Event Producer")
    public void produce(PersonEventDomain personEventDomain) throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
        PersonEvent personEvent = personEventMapper.toDto(personEventDomain);
        String value = objectMapper.writeValueAsString(personEvent);
        kafkaTemplate.send(topic, value).get(1, TimeUnit.SECONDS);
    }

}
