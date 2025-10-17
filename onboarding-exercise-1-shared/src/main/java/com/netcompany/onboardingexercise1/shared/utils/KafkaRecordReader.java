package com.netcompany.onboardingexercise1.shared.utils;

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
import org.springframework.stereotype.Component;

@Component
public class KafkaRecordReader {

    private final ObjectMapper objectMapper;

    private final Validator validator;

    public KafkaRecordReader(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public <K, T> T readConsumerRecord(ConsumerRecord<K, String> consumerRecord, Class<T> clazz) {
        T event;
        try {
            event = objectMapper.readValue(consumerRecord.value(), clazz);
        } catch (JsonProcessingException e) {
            throw new KafkaDeserializationException(buildDeserializationErrorResponse(consumerRecord, e));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            throw new KafkaValidationException(buildValidationErrorResponse(consumerRecord, violations));
        }

        return event;
    }

    private <K, T> EventErrorResponse buildValidationErrorResponse(ConsumerRecord<K, String> consumerRecord, Set<ConstraintViolation<T>> violations) {
        return EventErrorResponse.builder()
                                 .timestamp(Instant.now())
                                 .topic(consumerRecord.topic())
                                 .partition(consumerRecord.partition())
                                 .offset(consumerRecord.offset())
                                 .key(consumerRecord.key() != null ? consumerRecord.key().toString() : "null")
                                 .originalPayload(consumerRecord.value())
                                 .exceptionType("ValidationException")
                                 .message("Validation failed")
                                 .violations(violations.stream()
                                                       .map(violation -> Validation.builder()
                                                                                   .field(violation.getPropertyPath().toString())
                                                                                   .message(violation.getMessage())
                                                                                   .build())
                                                       .toList())
                                 .build();

    }

    private <K> EventErrorResponse buildDeserializationErrorResponse(ConsumerRecord<K, String> consumerRecord, Exception exception) {
        return EventErrorResponse.builder()
                                 .timestamp(Instant.now())
                                 .topic(consumerRecord.topic())
                                 .partition(consumerRecord.partition())
                                 .offset(consumerRecord.offset())
                                 .key(consumerRecord.key() != null ? consumerRecord.key().toString() : "null")
                                 .originalPayload(consumerRecord.value())
                                 .exceptionType(exception.getClass().getSimpleName())
                                 .message("Failed to deserialize JSON")
                                 .stackTrace(ExceptionUtils.getStackTrace(exception))
                                 .build();

    }
}