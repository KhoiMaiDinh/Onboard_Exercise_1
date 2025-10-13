package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.dto.EventErrorResponse;
import lombok.Getter;

@Getter
public class KafkaValidationException extends RuntimeException {
    private final transient com.netcompany.onboardingexercise1.shared.dto.EventErrorResponse error;

    public KafkaValidationException(EventErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }
}