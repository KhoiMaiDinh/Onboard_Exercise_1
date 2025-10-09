package com.netcompany.onboardingexercise1.adapter.inbound.rest.exception;

import com.netcompany.onboardingexercise1.core.exception.NotFoundException;
import com.netcompany.onboardingexercise1.rest.common.dto.ErrorResponse;
import com.netcompany.onboardingexercise1.rest.common.dto.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${app.debug.stacktrace:false}")
    private boolean includeStackTrace;

    private String getStackTrace(Throwable ex) {
        if (!includeStackTrace) {
            return null;
        }
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<ValidationError> violations =
                ex.getConstraintViolations().stream().map(v -> new ValidationError(v.getPropertyPath().toString(), v.getMessage())).toList();
        String stackTrace = getStackTrace(ex);
        ErrorResponse error =
                new ErrorResponse(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.toString(), "Validation failed", request.getRequestURI(), violations,
                        stackTrace);

        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationError> violations =
                ex.getBindingResult().getFieldErrors().stream().map(err -> new ValidationError(err.getField(), err.getDefaultMessage())).toList();
        String stackTrace = getStackTrace(ex);
        ErrorResponse error =
                new ErrorResponse(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.toString(), "Validation failed", request.getRequestURI(), violations,
                        stackTrace);
        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String stackTrace = getStackTrace(ex);
        ErrorResponse error =
                new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(), ex.getMessage(), request.getRequestURI(), null, stackTrace);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        String stackTrace = getStackTrace(ex);
        ErrorResponse error =
                new ErrorResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI(), null, stackTrace);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        String stackTrace = getStackTrace(ex);
        ErrorResponse error = new ErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Unexpected error occurred",
                request.getRequestURI(), null, stackTrace);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
