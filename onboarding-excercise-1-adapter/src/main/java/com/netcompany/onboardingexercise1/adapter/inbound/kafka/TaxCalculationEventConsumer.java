package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.core.service.person.PersonService;
import com.netcompany.onboardingexercise1.event.taxcalculationevent.TaxCalculationEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaxCalculationEventConsumer {
    private final PersonService personService;
    private final ObjectMapper objectMapper;

    public TaxCalculationEventConsumer(PersonService personService, ObjectMapper objectMapper) {
        this.personService = personService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@environment.getProperty('onboarding-exercise-1.kafka.inbound.tax-calculation-topic')}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        String value = consumerRecord.value();

        TaxCalculationEvent taxCalculationEvent = objectMapper.readValue(value, TaxCalculationEvent.class);

        personService.handleTaxCalculation(taxCalculationEvent.getTaxNumber(), taxCalculationEvent.getCalculatedTax());
    }
}
