package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.adapter.mapper.PersonEventMapper;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.service.person.PersonService;
import com.netcompany.onboardingexercise1.event.personevent.PersonEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonEventConsumer {
    private final PersonService personService;

    private final ObjectMapper objectMapper;

    private final PersonEventMapper personEventMapper;

    public PersonEventConsumer(PersonService personService, ObjectMapper objectMapper, PersonEventMapper personEventMapper) {
        this.personService = personService;
        this.objectMapper = objectMapper;
        this.personEventMapper = personEventMapper;
    }


    @KafkaListener(topics = "${onboarding-exercise-1.kafka.inbound.person-topic}", containerFactory = "personKafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        String value = consumerRecord.value();

        PersonEvent personEvent = objectMapper.readValue(value, PersonEvent.class);

        PersonDomain personDomain = personEventMapper.toDomain(personEvent).getPersonDomain();

        switch (personEvent.getPersonEventType()) {
            case CREATE:
                personService.save(personDomain);
                break;
            case UPDATE:
                personService.update(personDomain.getId(), personDomain);
                break;
            case DELETE:
                personService.delete(personDomain.getId());
                break;
            default:
                throw new IllegalArgumentException("Unknown person event type");
        }
    }
}
