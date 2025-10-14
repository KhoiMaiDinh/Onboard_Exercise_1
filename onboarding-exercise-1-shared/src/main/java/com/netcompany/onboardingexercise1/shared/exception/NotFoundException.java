package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode, String resourceName, Object identifier) {

        super(errorCode, String.format("%s with identifier '%s' not found", resourceName, identifier), HttpStatus.NOT_FOUND);
    }

}
