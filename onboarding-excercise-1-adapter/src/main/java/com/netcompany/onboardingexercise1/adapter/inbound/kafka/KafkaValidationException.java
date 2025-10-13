package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.netcompany.onboardingexercise1.rest.common.dto.EventErrorResponse;
import lombok.Getter;

@Getter
public class KafkaValidationException extends RuntimeException {
    private final transient EventErrorResponse error;

    public KafkaValidationException(EventErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }
}