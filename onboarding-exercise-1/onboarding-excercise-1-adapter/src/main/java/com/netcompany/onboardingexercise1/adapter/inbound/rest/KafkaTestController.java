package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.event.taxcalculationevent.TaxCalculationEvent;
import com.netcompany.onboardingexercise1.event.testevent.TestEvent;
import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import com.netcompany.onboardingexercise1.shared.exception.UnexpectedException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaTestController {
    @Autowired
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}")
    private String taxCalculationTopic;

    @Value("${onboarding-exercise-1.kafka.inbound.test-topic}")
    private String testEventTopic;

    @PostMapping("/tax-calculations")
    public String sendTestTaxEvent(@RequestBody TaxCalculationEvent event) throws JsonProcessingException {
        log.info("Sending test TaxCalculationEvent to Kafka {} topic {}", event, taxCalculationTopic);
        kafkaTemplate.send(taxCalculationTopic, objectMapper.writeValueAsString(event));
        return "Event sent to topic: " + taxCalculationTopic;
    }

    @PostMapping("/tax-calculations-events/batch")
    public String sendTaxCalculationEvents(@RequestBody List<TaxCalculationEvent> taxCalculationEvents) {
        if (taxCalculationEvents == null || taxCalculationEvents.isEmpty()) {
            return "No taxCalculationEvents to send.";
        }

        log.info("Producing batch of {} TaxCalculationEvent to topic {}", taxCalculationEvents.size(), taxCalculationTopic);



        List<CompletableFuture<SendResult<Integer, String>>> futures = taxCalculationEvents.stream().map(taxCalculationEvent -> {
            try {
                return kafkaTemplate.send(taxCalculationTopic, objectMapper.writeValueAsString(taxCalculationEvent)).toCompletableFuture();
            } catch (JsonProcessingException e) {
                throw new UnexpectedException(ErrorCode.ONBOARDING_UNEXPECTED_001, "Exception while producing kafka message", e);
            }
        }).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return "Sent " + taxCalculationEvents.size() + " taxCalculationEvents to topic: " + taxCalculationTopic;
    }

    @PostMapping("/test-events/batch")
    public String sendTestEvents(@RequestBody List<TestEvent> testEvents) {
        if (testEvents == null || testEvents.isEmpty()) {
            return "No testEvents to send.";
        }

        log.info("Producing batch of {} TestEvents to topic {}", testEvents.size(), testEventTopic);



        List<CompletableFuture<SendResult<Integer, String>>> futures = testEvents.stream().map(testEvent -> {
            try {
                return kafkaTemplate.send(testEventTopic, testEvent.getId().intValue(), objectMapper.writeValueAsString(testEvent)).toCompletableFuture();
            } catch (JsonProcessingException e) {
                throw new UnexpectedException(ErrorCode.ONBOARDING_UNEXPECTED_001, "Exception while producing kafka message", e);
            }
        }).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return "Sent " + testEvents.size() + " testEvents to topic: " + testEventTopic;
    }
}
