package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.dto.EventErrorResponse;
import lombok.Getter;

@Getter
public class KafkaValidationException extends RuntimeException {
    private final transient EventErrorResponse eventErrorResponse;

    public KafkaValidationException(EventErrorResponse eventErrorResponse) {
        super(eventErrorResponse.getMessage() + eventErrorResponse.getViolations());
        this.eventErrorResponse = eventErrorResponse;
    }
}