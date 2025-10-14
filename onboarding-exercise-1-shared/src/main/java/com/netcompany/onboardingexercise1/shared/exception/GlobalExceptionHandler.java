package com.netcompany.onboardingexercise1.shared.exception;

import com.netcompany.onboardingexercise1.shared.dto.RestErrorResponse;
import com.netcompany.onboardingexercise1.shared.dto.Validation;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${app.debug.stacktrace:false}")
    private boolean includeStackTrace;

    private String getStackTrace(Throwable throwable) {
        return includeStackTrace ? ExceptionUtils.getStackTrace(throwable) : null;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest req) {
        RestErrorResponse resp = RestErrorResponse.builder()
                                                  .timestamp(Instant.now())
                                                  .status(HttpStatus.NOT_FOUND.value())
                                                  .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                                  .message("API path not found: " + req.getRequestURI())
                                                  .path(req.getRequestURI())
                                                  .stackTrace(getStackTrace(ex))
                                                  .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String paramName = ex.getName();
        Object value = ex.getValue();
        String paramValue = (value != null) ? value.toString() : "null";

        RestErrorResponse resp = RestErrorResponse.builder()
                                                  .timestamp(Instant.now())
                                                  .status(HttpStatus.BAD_REQUEST.value())
                                                  .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                                  .message(String.format("Parameter '%s' has invalid value '%s'. Expected a valid integer.", paramName, paramValue))
                                                  .path(req.getRequestURI())
                                                  .stackTrace(getStackTrace(ex))
                                                  .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<Validation> violations = ex.getConstraintViolations()
                                        .stream()
                                        .map(violation -> Validation.builder()
                                                                    .field(violation.getPropertyPath().toString())
                                                                    .message(violation.getMessage())
                                                                    .build())
                                        .toList();
        RestErrorResponse error = RestErrorResponse.builder()
                                                   .timestamp(Instant.now())
                                                   .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                                                   .error(HttpStatus.UNPROCESSABLE_ENTITY.toString())
                                                   .message("Validation failed")
                                                   .path(request.getRequestURI())
                                                   .violations(violations)
                                                   .stackTrace(getStackTrace(ex))
                                                   .build();

        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<Validation> violations = ex.getBindingResult()
                                        .getFieldErrors()
                                        .stream()
                                        .map(fieldError -> Validation.builder().field(fieldError.getField()).message(fieldError.getDefaultMessage()).build())
                                        .toList();

        RestErrorResponse error = RestErrorResponse.builder()
                                                   .timestamp(Instant.now())
                                                   .status(HttpStatus.BAD_REQUEST.value())
                                                   .error(HttpStatus.BAD_REQUEST.toString())
                                                   .message("Invalid Params")
                                                   .path(request.getRequestURI())
                                                   .violations(violations)
                                                   .stackTrace(getStackTrace(ex))
                                                   .build();

        return ResponseEntity.unprocessableEntity().body(error);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        RestErrorResponse error = RestErrorResponse.builder()
                                                   .timestamp(Instant.now())
                                                   .status(HttpStatus.BAD_REQUEST.value())
                                                   .error(HttpStatus.BAD_REQUEST.toString())
                                                   .message(ex.getMessage())
                                                   .path(request.getRequestURI())
                                                   .violations(null)
                                                   .stackTrace(getStackTrace(ex))
                                                   .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        RestErrorResponse error = RestErrorResponse.builder()
                                                   .timestamp(Instant.now())
                                                   .status(HttpStatus.NOT_FOUND.value())
                                                   .error(HttpStatus.NOT_FOUND.toString())
                                                   .message(ex.getMessage())
                                                   .path(request.getRequestURI())
                                                   .stackTrace(getStackTrace(ex))
                                                   .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleUnexpectedError(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        RestErrorResponse error = RestErrorResponse.builder()
                                                   .timestamp(Instant.now())
                                                   .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                   .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                                                   .message("Unexpected error occurred")
                                                   .path(request.getRequestURI())
                                                   .stackTrace(getStackTrace(ex))
                                                   .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
