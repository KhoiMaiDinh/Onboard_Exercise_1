package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ImmutableFieldException extends BaseException {

    public ImmutableFieldException(ErrorCode errorCode, String fieldName) {
        super(errorCode, String.format("Field '%s' cannot be changed.", fieldName), HttpStatus.CONFLICT);
    }

}