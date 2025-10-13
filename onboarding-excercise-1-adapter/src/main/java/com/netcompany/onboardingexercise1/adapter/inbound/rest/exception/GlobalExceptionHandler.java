package com.netcompany.onboardingexercise1.adapter.inbound.rest.exception;

import com.netcompany.onboardingexercise1.core.exception.NotFoundException;
import com.netcompany.onboardingexercise1.rest.common.dto.RestErrorResponse;
import com.netcompany.onboardingexercise1.rest.common.dto.Validation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${app.debug.stacktrace:false}")
    private boolean includeStackTrace;


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<Validation> violations = ex.getConstraintViolations()
                                        .stream()
                                        .map(violation -> Validation.builder()
                                                                    .field(violation.getPropertyPath().toString())
                                                                    .message(violation.getMessage())
                                                                    .build())
                                        .toList();
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        RestErrorResponse error =
                new RestErrorResponse(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.toString(), "Validation failed",
                        request.getRequestURI(), violations, stackTrace);
        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        RestErrorResponse error =
                new RestErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(), ex.getMessage(), request.getRequestURI(),
                        null, stackTrace);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        RestErrorResponse error =
                new RestErrorResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI(), null, stackTrace);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleUnexpectedError(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        RestErrorResponse error = new RestErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Unexpected error occurred", request.getRequestURI(), null, stackTrace);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
