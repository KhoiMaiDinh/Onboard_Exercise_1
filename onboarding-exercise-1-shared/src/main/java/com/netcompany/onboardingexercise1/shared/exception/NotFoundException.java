package com.netcompany.onboardingexercise1.shared.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with identifier '%s' not found", resourceName, identifier));
    }

}
