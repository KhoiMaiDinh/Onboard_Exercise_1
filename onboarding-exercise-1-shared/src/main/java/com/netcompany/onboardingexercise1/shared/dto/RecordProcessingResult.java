package com.netcompany.onboardingexercise1.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecordProcessingResult {
    private final boolean success;

    private final int failedIndex;

    private final Throwable exception;
}