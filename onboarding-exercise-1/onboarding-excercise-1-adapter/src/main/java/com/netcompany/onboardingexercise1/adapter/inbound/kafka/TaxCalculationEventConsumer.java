package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.core.service.person.PersonService;
import com.netcompany.onboardingexercise1.event.taxcalculationevent.TaxCalculationEvent;
import com.netcompany.onboardingexercise1.shared.kafka.consumer.AbstractKafkaConsumer;
import jakarta.validation.Validator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class TaxCalculationEventConsumer extends AbstractKafkaConsumer<TaxCalculationEvent> {

    private final ExecutorService executorService;

    private final PersonService personService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}")
    private String taxCalculationTopic;

    public TaxCalculationEventConsumer(PersonService personService, ObjectMapper objectMapper, Validator validator, KafkaTemplate<String, String> kafkaTemplate,
            ExecutorService executorService) {
        super(objectMapper, validator);

        this.personService = personService;
        this.kafkaTemplate = kafkaTemplate;
        this.executorService = executorService;
    }


    @Override
    @KafkaListener(topics = "${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}", containerFactory = "taxCalculationKafkaListenerContainerFactory")
    protected void consume(ConsumerRecord<String, String> consumerRecord) {
        TaxCalculationEvent taxCalculationEvent = readConsumerRecord(consumerRecord, TaxCalculationEvent.class);
        personService.handleTaxCalculation(taxCalculationEvent.getTaxNumber(), taxCalculationEvent.getCalculatedTax());
    }

    @Override
    @KafkaListener(topics = "#{@environment.getProperty('onboarding-exercise-1.kafka.inbound.tax-calculation-topic')}.RETRY", containerFactory = "taxCalculationRetryKafkaListenerContainerFactory")
    protected void retryConsume(ConsumerRecord<String, String> consumerRecord) {
        log.debug("Retry Consumer Record: {}", consumerRecord);
        TaxCalculationEvent taxCalculationEvent = readConsumerRecord(consumerRecord, TaxCalculationEvent.class);

        personService.handleTaxCalculation(taxCalculationEvent.getTaxNumber(), taxCalculationEvent.getCalculatedTax());
    }

    //    @KafkaListener(topics = "${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}", containerFactory = "taxCalculationNonBlockingBatchKafkaListenerContainerFactory")
    public void consumeNonBlockingBatch(List<ConsumerRecord<String, String>> consumerRecords, Acknowledgment ack) {

        for (final ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            CompletableFuture.runAsync(() -> {
                TaxCalculationEvent taxCalculationEvent = readConsumerRecord(consumerRecord, TaxCalculationEvent.class);
                personService.handleTaxCalculation(taxCalculationEvent.getTaxNumber(), taxCalculationEvent.getCalculatedTax());
                log.debug("Non Blocking Batch ConsumerRecord: {}", taxCalculationEvent);
                if (taxCalculationEvent.getTaxNumber().equals("555")) {
                    throw new RuntimeException("Simulate error");
                }
            }, executorService).handle((res, ex) -> {
                if (ex != null)
                    kafkaTemplate.send(taxCalculationTopic.concat(".RETRY"), consumerRecord.value());
                return res;
            });
        }

        ack.acknowledge();
    }

    //    @KafkaListener(topics = "${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}", containerFactory = "taxCalculationBlockingBatchKafkaListenerContainerFactory")
    @Transactional
    public void consumeBlockingBatch(List<ConsumerRecord<String, String>> consumerRecords, Acknowledgment ack) {

        for (int i = 0; i < consumerRecords.size(); i++) {
            final ConsumerRecord<String, String> consumerRecord = consumerRecords.get(i);

            try {
                TaxCalculationEvent taxCalculationEvent = readConsumerRecord(consumerRecord, TaxCalculationEvent.class);
                personService.handleTaxCalculation(taxCalculationEvent.getTaxNumber(), taxCalculationEvent.getCalculatedTax());
                log.debug("Blocking Batch ConsumerRecord: {}", taxCalculationEvent);
            } catch (Exception exception) {
                throw new BatchListenerFailedException("Processing failed at record index " + i, exception, i);
            }
        }

        ack.acknowledge();

    }
}
