package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnexpectedException extends BaseException {
    public UnexpectedException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}