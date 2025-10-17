package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    private final HttpStatus httpStatus;

    protected BaseException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = status;
    }

    protected BaseException(ErrorCode errorCode, String message, HttpStatus status,  Throwable cause) {
        super(message,  cause);
        this.errorCode = errorCode;
        this.httpStatus = status;
    }

}
