package com.netcompany.onboardingexercise1.shared.kafka.consumer;

import com.netcompany.onboardingexercise1.shared.utils.KafkaRecordReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class AbstractKafkaConsumer<T> {

    private final KafkaRecordReader kafkaRecordReader;

    protected AbstractKafkaConsumer(KafkaRecordReader kafkaRecordReader) {
        this.kafkaRecordReader = kafkaRecordReader;
    }

    protected abstract void consume(ConsumerRecord<String, String> consumerRecord);

    protected abstract void retryConsume(ConsumerRecord<String, String> consumerRecord);

    protected T readConsumerRecord(ConsumerRecord<String, String> consumerRecord, Class<T> clazz) {
        return kafkaRecordReader.readConsumerRecord(consumerRecord, clazz);
    }
}
