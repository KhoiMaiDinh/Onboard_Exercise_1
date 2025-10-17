package com.netcompany.onboardingexercise1.shared.exception;

import org.springframework.http.HttpStatus;
import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;

public class DuplicationException extends BaseException {
    public DuplicationException(ErrorCode errorCode, String entity, String field, String value) {
        super(errorCode, String.format("%s with %s '%s' already exists.", entity, field, value), HttpStatus.CONFLICT);
    }
}
