package com.netcompany.onboardingexercise1.shared.kafka.config;

import com.netcompany.onboardingexercise1.shared.exception.KafkaDeserializationException;
import com.netcompany.onboardingexercise1.shared.exception.KafkaValidationException;
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

    protected final KafkaTemplate<Object, Object> kafkaTemplate;

    protected final KafkaProperties kafkaProperties;

    protected final String topic;

    protected List<Class<? extends Exception>> nonRetryableExceptions =
            List.of(IllegalArgumentException.class, NullPointerException.class, KafkaDeserializationException.class, KafkaValidationException.class);

    protected AbstractKafkaConsumerConfig(String topic, KafkaTemplate<Object, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.topic = topic;
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
        return getNonRetryableExceptions().stream().anyMatch(exception -> exception.isAssignableFrom(ex.getClass()));
    }

    protected List<Class<? extends Exception>> getNonRetryableExceptions() {
        return nonRetryableExceptions;
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
                logException("DLT", root, consumerRecord);
                return new TopicPartition(getDeadLetterTopic(), consumerRecord.partition());
            } else {
                logException("RETRY", root, consumerRecord);
                return new TopicPartition(getRetryTopic(), consumerRecord.partition());
            }
        });
    }

    protected DeadLetterPublishingRecoverer retryPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, exception) -> {
            Throwable root = unwrap(exception);
            logException("DLT (from retry)", root, consumerRecord);
            return new TopicPartition(getDeadLetterTopic(), consumerRecord.partition());
        });
    }

    public DefaultErrorHandler mainErrorHandler() {
        return mainErrorHandler(0L, 0L);
    }

    public DefaultErrorHandler mainErrorHandler(long intervalMs, long maxAttempts) {
        FixedBackOff backOff = new FixedBackOff(intervalMs, maxAttempts);
        return new DefaultErrorHandler(mainPublishingRecoverer(), backOff);
    }

    public DefaultErrorHandler retryErrorHandler() {
        return retryErrorHandler(2, 1000L, 2.0, 2000L);
    }

    public DefaultErrorHandler retryErrorHandler(
            int maxRetries,
            long initialIntervalMs,
            double multiplier,
            long maxIntervalMs
    ) {
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(maxRetries);
        backOff.setInitialInterval(initialIntervalMs);
        backOff.setMultiplier(multiplier);
        backOff.setMaxInterval(maxIntervalMs);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(retryPublishingRecoverer(), backOff);
        nonRetryableExceptions.forEach(errorHandler::addNotRetryableExceptions);
        return errorHandler;
    }

    private void logException(String destination, Throwable exception, org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> consumerRecord) {
        String message = String.format("Routing to %s due to exception [%s] for record [topic=%s, partition=%d, offset=%d, key=%s]",
                destination,
                exception.getClass().getSimpleName() + ": " + exception.getMessage(),
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key());

        if ("RETRY".equals(destination)) {
            log.info(message, exception);
        } else {
            log.error(message, exception);
        }
    }
}