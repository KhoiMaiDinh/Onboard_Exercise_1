package com.netcompany.onboardingexercise1.shared.exception;

public class DuplicationException extends RuntimeException {
    public DuplicationException(String entity, String field, String value) {
        super(String.format("%s with %s '%s' already exists.", entity, field, value));
    }
}
