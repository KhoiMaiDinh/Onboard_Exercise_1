package com.netcompany.onboardingexercise1.shared.annotation.aspect;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Pointcut("@annotation(com.netcompany.onboardingexercise1.shared.annotation.aspect.Audit)")
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

        // Detect context
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
        log.info("AUDIT RESULT: {}", result);
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
                        (arg instanceof String string && string.contains("kafka"))
        );
    }

    private void logKafkaContext(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String message) {
                log.info("Kafka Payload: {}", message);
            } else if (arg instanceof org.springframework.messaging.Message<?> kafkaMessage) {
                log.info("Kafka Message Headers: {}", kafkaMessage.getHeaders());
                log.info("Kafka Message Payload: {}", kafkaMessage.getPayload());
            }
        }
    }
}
