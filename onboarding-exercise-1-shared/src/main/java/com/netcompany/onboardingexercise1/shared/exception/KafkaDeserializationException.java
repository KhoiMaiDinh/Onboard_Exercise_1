package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.dto.EventErrorResponse;
import lombok.Getter;

@Getter
public class KafkaDeserializationException extends RuntimeException {
    private final transient EventErrorResponse error;

    public KafkaDeserializationException(EventErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }
}
