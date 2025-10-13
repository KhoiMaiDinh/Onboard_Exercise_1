package com.netcompany.onboardingexercise1.shared.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcompany.onboardingexercise1.shared.dto.EventErrorResponse;
import com.netcompany.onboardingexercise1.shared.dto.Validation;
import com.netcompany.onboardingexercise1.shared.exception.KafkaDeserializationException;
import com.netcompany.onboardingexercise1.shared.exception.KafkaValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.Set;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class AbstractKafkaConsumer<T> {

    private final ObjectMapper objectMapper;

    private final Validator validator;

    protected AbstractKafkaConsumer(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    protected abstract void consume(ConsumerRecord<String, String> consumerRecord);
    protected abstract void retryConsume(ConsumerRecord<String, String> consumerRecord);

    protected T readConsumerRecord(ConsumerRecord<String, String> consumerRecord, Class<T> clazz) {
        T event;
        try {
            event = objectMapper.readValue(consumerRecord.value(), clazz);
        } catch (JsonProcessingException e) {
            throw buildEventErrorResponse(consumerRecord, e);
        }

        Set<ConstraintViolation<T>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            throw new KafkaValidationException(EventErrorResponse.builder()
                                                                 .timestamp(Instant.now())
                                                                 .topic(consumerRecord.topic())
                                                                 .partition(consumerRecord.partition())
                                                                 .offset(consumerRecord.offset())
                                                                 .key(consumerRecord.key() != null ? consumerRecord.key() : null)
                                                                 .originalPayload(consumerRecord.value() != null ? consumerRecord.value() : null)
                                                                 .exceptionType("ValidationException")
                                                                 .message("Validation failed")
                                                                 .violations(violations.stream()
                                                                                       .map(violation -> Validation.builder()
                                                                                                                   .field(violation.getPropertyPath()
                                                                                                                                   .toString())
                                                                                                                   .message(violation.getMessage())
                                                                                                                   .build())
                                                                                       .toList())
                                                                 .build());
        }

        return event;
    }

    private KafkaDeserializationException buildEventErrorResponse(ConsumerRecord<String, String> consumerRecord, Exception exception) {
        EventErrorResponse error = EventErrorResponse.builder()
                                                     .timestamp(Instant.now())
                                                     .topic(consumerRecord.topic())
                                                     .partition(consumerRecord.partition())
                                                     .offset(consumerRecord.offset())
                                                     .key(consumerRecord.key())
                                                     .originalPayload(consumerRecord.value())
                                                     .exceptionType(exception.getClass().getSimpleName())
                                                     .message("Failed to deserialize JSON")
                                                     .stackTrace(ExceptionUtils.getStackTrace(exception))
                                                     .build();

        return new KafkaDeserializationException(error);
    }
}
