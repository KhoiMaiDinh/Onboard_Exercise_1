package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.adapter.mapper.PersonMapper;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.port.outbound.persistence.PersonPort;
import com.netcompany.onboardingexercise1.event.personevent.PersonEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonEventConsumer {
    private final PersonPort personPort;

    private final ObjectMapper objectMapper;

    private final PersonMapper personMapper;

    public PersonEventConsumer(PersonPort personPort, ObjectMapper objectMapper, PersonMapper personMapper) {
        this.personPort = personPort;
        this.objectMapper = objectMapper;
        this.personMapper = personMapper;
    }


    @KafkaListener(topics = "#{@environment.getProperty('onboarding-exercise-1.kafka.inbound.person-topic')}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        String value = consumerRecord.value();

        PersonEvent personEvent = objectMapper.readValue(value, PersonEvent.class);

        PersonDomain personDomain = personMapper.dtoToDomain(personEvent.getPerson());

        switch (personEvent.getPersonEventType()) {
            case CREATE:
                personPort.save(personMapper.dtoToDomain(personEvent.getPerson()));
                break;
            case UPDATE:
                personPort.update(personDomain);
                break;
            case DELETE:
                personPort.delete(personDomain.getId());
                break;
            default:
                throw new IllegalArgumentException("Unknown person event type");
        }
    }
}
