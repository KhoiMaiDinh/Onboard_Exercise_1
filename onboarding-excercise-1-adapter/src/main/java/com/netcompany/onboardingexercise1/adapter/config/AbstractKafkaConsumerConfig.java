package com.netcompany.onboardingexercise1.adapter.config;

import com.netcompany.onboardingexercise1.adapter.inbound.kafka.KafkaDeserializationException;
import com.netcompany.onboardingexercise1.adapter.inbound.kafka.KafkaValidationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
public abstract class AbstractKafkaConsumerConfig {

    protected final List<Class<? extends Exception>> nonRetryableExceptions =
            List.of(IllegalArgumentException.class, NullPointerException.class, KafkaDeserializationException.class, KafkaValidationException.class);

    protected final KafkaTemplate<Object, Object> kafkaTemplate;

    protected final KafkaProperties kafkaProperties;

    protected final String topic;

    protected AbstractKafkaConsumerConfig(String baseTopic, KafkaTemplate<Object, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.topic = baseTopic;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    protected String getRetryTopic() {
        return topic + ".RETRY";
    }

    protected String getDeadLetterTopic() {
        return topic + ".DLT";
    }

    protected boolean isNonRetryable(Throwable ex) {
        return nonRetryableExceptions.stream().anyMatch(exception -> exception.isAssignableFrom(ex.getClass()));
    }

    protected Throwable unwrap(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

    protected DeadLetterPublishingRecoverer mainPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, exception) -> {
            Throwable root = unwrap(exception);
            if (isNonRetryable(root)) {
                return new TopicPartition(getDeadLetterTopic(), consumerRecord.partition());
            } else {
                return new TopicPartition(getRetryTopic(), consumerRecord.partition());
            }
        });
    }

    protected DeadLetterPublishingRecoverer retryPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, exception) -> new TopicPartition(getDeadLetterTopic(), consumerRecord.partition()));
    }

    public DefaultErrorHandler mainErrorHandler() {
        return new DefaultErrorHandler(mainPublishingRecoverer(), new FixedBackOff(0, 0));
    }

    public DefaultErrorHandler retryErrorHandler() {
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(2);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(2000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(retryPublishingRecoverer(), backOff);
        nonRetryableExceptions.forEach(errorHandler::addNotRetryableExceptions);
        return errorHandler;
    }
}