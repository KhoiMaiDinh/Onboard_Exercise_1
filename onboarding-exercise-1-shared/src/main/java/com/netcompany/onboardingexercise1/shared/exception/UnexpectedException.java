package com.netcompany.onboardingexercise1.shared.exception;

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}