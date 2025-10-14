package com.netcompany.onboardingexercise1.shared.enums;

import lombok.Getter;

/**
 * Central registry of error codes for the onboarding application.
 *
 * <p>Each enum constant represents a unique error scenario, with:
 * 1. A machine‑friendly code (e.g. "ONBOARD‑VALIDATION‑001")
 * 2. A human‑friendly default message (can be overridden)
 *
 * <p>
 * Structure: [MODULE]_[ISSUE]_[CODE]
 *
 * <p>This enum makes it easy to:
 * - Reference error codes from your exceptions
 * - Keep codes consistent
 * - Document codes in one place
 * - Allow automated mapping in your error handlers
 */
public enum ErrorCode {
    COMMON_NOTFOUND_001("COMMON_NOTFOUND_001", "API path not found"),

    COMMON_VALIDATION_001("COMMON_VALIDATION_001", "Request parameter has invalid type"),
    COMMON_VALIDATION_002("COMMON_VALIDATION_002", "Constraint violation detected"),
    COMMON_VALIDATION_003("COMMON_VALIDATION_003", "Invalid request body"),
    COMMON_VALIDATION_004("COMMON_VALIDATION_004", "Illegal argument provided"),

    COMMON_INTERNAL_SERVER_ERROR_001("COMMON_INTERNAL_SERVER_ERROR_001", "Internal server error"),

    ONBOARDING_DUPLICATION_001("ONBOARDING_DUPLICATION_001", "Duplicate taxNumber in PERSON"),

    ONBOARDING_NOTFOUND_001("ONBOARDING_NOTFOUND_001", "PERSON not found by id"),
    ONBOARDING_NOTFOUND_002("ONBOARDING_NOTFOUND_002", "PERSON not found by taxNumber"),


    ONBOARDING_UNEXPECTED_001("ONBOARDING_UNEXPECTED_001", "Create manual kafka consumer"),

    ONBOARDING_IMMUTABLE_001("ONBOARDING_IMMUTABLE_001", "Immutable field taxNumber of PERSON");



    @Getter
    private final String code;

    @Getter
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Format a full user message based on this error code.
     *
     * @param detail optional more context (e.g. which field, which identifier)
     * @return formatted message combining default and extra detail
     */
    public String formatMessage(String detail) {
        if (detail == null || detail.isBlank()) {
            return defaultMessage;
        }
        return defaultMessage + ": " + detail;
    }

    @Override
    public String toString() {
        return code;
    }
}
