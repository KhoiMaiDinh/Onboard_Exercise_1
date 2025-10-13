package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.event.taxcalculationevent.TaxCalculationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaTestController {
    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}")
    private String taxCalculationTopic;

    @PostMapping("/tax-calculation")
    public String sendTestTaxEvent(@RequestBody TaxCalculationEvent event) throws JsonProcessingException {
        log.info("Sending test TaxCalculationEvent to Kafka {} topic {}", event, taxCalculationTopic);
        kafkaTemplate.send(taxCalculationTopic, objectMapper.writeValueAsString(event));
        return "Event sent to topic: " + taxCalculationTopic;
    }
}
