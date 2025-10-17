package com.netcompany.onboardingexercise1.shared.annotation.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    private final ObjectMapper objectMapper;

    public AuditAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(Audit)")
    public void auditPointcut() {}

    @Before("auditPointcut()")
    public void audit(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Audit audit = method.getAnnotation(Audit.class);
        String action = audit.action();

        log.info("---- AUDIT START [{}] ----", action);
        log.info("Method: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        log.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));

        if (isHttpRequestContextAvailable()) {
            logHttpRequest();
        } else if (isKafkaContext(joinPoint)) {
            logKafkaContext(joinPoint);
        } else {
            log.info("Unknown context (neither HTTP nor Kafka detected).");
        }

        log.info("---- AUDIT END [{}] ----", action);
    }

    @AfterReturning(pointcut = "auditPointcut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result) {
        Object payload = (result instanceof ResponseEntity<?> response) ? response.getBody() : result;

        Object content = (payload instanceof Page<?> page)
                ? page.getContent()
                : payload;

        try {
            log.info("AUDIT RESULT: {}", objectMapper.writeValueAsString(content));
        } catch (Exception e) {
            log.warn("Failed to serialize audit result", e);
            log.info("AUDIT RESULT (raw): {}", content);
        }
    }

    @AfterThrowing(pointcut = "auditPointcut()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Audit audit = method.getAnnotation(Audit.class);
        String action = audit.action();

        log.error("---- AUDIT EXCEPTION [{}] ----", action);
        log.error("Method: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        log.error("Arguments: {}", Arrays.toString(joinPoint.getArgs()));
        log.error("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }

    private boolean isHttpRequestContextAvailable() {
        try {
            return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes;
        } catch (Exception e) {
            return false;
        }
    }

    private void logHttpRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("HTTP METHOD: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Headers: {}", Collections.list(request.getHeaderNames()).stream()
                                           .collect(Collectors.toMap(h -> h, request::getHeader)));
    }

    private boolean isKafkaContext(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs()).anyMatch(arg ->
                arg instanceof org.apache.kafka.common.header.Headers ||
                        arg instanceof org.springframework.messaging.Message<?> ||
                        arg instanceof org.apache.kafka.clients.consumer.ConsumerRecord
        );
    }

    private void logKafkaContext(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ConsumerRecord<?, ?> consumerRecord) {
                String topic = consumerRecord.topic();
                int partition = consumerRecord.partition();
                long offset = consumerRecord.offset();
                Object payload = consumerRecord.value();
                log.info("Kafka -> topic={}, partition={}, offset={}, payload={}",
                        topic, partition, offset, payload);
            } else if (arg instanceof org.springframework.messaging.Message<?> message) {
                log.info("Kafka Message Headers: {}", message.getHeaders());
                log.info("Kafka Message Payload: {}", message.getPayload());
            }
        }
    }
}
