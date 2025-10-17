package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.netcompany.onboardingexercise1.event.testevent.TestEvent;
import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import com.netcompany.onboardingexercise1.shared.exception.KafkaDeserializationException;
import com.netcompany.onboardingexercise1.shared.exception.KafkaValidationException;
import com.netcompany.onboardingexercise1.shared.exception.UnexpectedException;
import com.netcompany.onboardingexercise1.shared.utils.KafkaRecordReader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class BatchTestEventService {

    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);
    private static final Integer MAX_POLL_RECORDS = 100;
    private static final int QUEUE_CAPACITY = 1000;

    private final String topic;
    private final String bootstrapServers;
    private final String consumerGroupId;

    private final KafkaRecordReader kafkaRecordReader;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    private final BlockingQueue<TestEvent> eventQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private volatile boolean running = true;
    private Thread consumerThread;

    public BatchTestEventService(
            @Value("${onboarding-exercise-1.kafka.inbound.test-topic}") String topic,
            @Value("${spring.kafka.consumer.bootstrap-servers}") String bootstrapServers,
            @Value("${onboarding-exercise-1.kafka.manual-consumer-group-id}") String consumerGroupId,
            KafkaRecordReader kafkaRecordReader, KafkaTemplate<Integer, String> kafkaTemplate
    ) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.consumerGroupId = consumerGroupId;
        this.kafkaRecordReader = kafkaRecordReader;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void startConsumerThread() {
        consumerThread = new Thread(this::consumeLoop, "kafka-consumer-thread");
        consumerThread.setDaemon(true);
        consumerThread.start();
        log.info("Started Kafka consumer thread for topic {}", topic);
    }

    @PreDestroy
    public void stopConsumerThread() {
        running = false;
        if (consumerThread != null) {
            consumerThread.interrupt();
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Stopped Kafka consumer thread for topic {}", topic);
    }

    private boolean offerEventToQueue(TestEvent event) {
        try {
            eventQueue.put(event);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void processRecord(ConsumerRecord<Integer, String> consumerRecord, List<ConsumerRecord<Integer, String>> failedRecords, Map<TopicPartition, Long> lastOffsets) {
        try {
            TestEvent testEvent = parseRecord(consumerRecord);
            if (testEvent.getId().toString().equals("55"))
                throw new RuntimeException("Simulate exception");
            if (!offerEventToQueue(testEvent)) {
                running = false;
                return;
            }
        } catch (Exception ex) {
            log.error("Error processing record at offset {}:{}", consumerRecord.partition(), consumerRecord.offset(), ex);
            failedRecords.add(consumerRecord);
        }
        lastOffsets.put(
                new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                consumerRecord.offset() + 1
        );
    }


    private void pushToRetryQueue(ConsumerRecord<Integer, String> consumerRecord) {
        kafkaTemplate.send(topic.concat(".RETRY"), consumerRecord.value());
        log.warn("Pushed to retry queue: partition={}, offset={}", consumerRecord.partition(), consumerRecord.offset());
    }

    private void consumeLoop() {
        KafkaConsumer<Integer, String> consumer = createConsumer();
        List<TopicPartition> partitions = getTopicPartitions(consumer);
        consumer.assign(partitions);

        try {
            while (running) {
                ConsumerRecords<Integer, String> consumerRecords = consumer.poll(POLL_TIMEOUT);
                log.debug("Polled {} consumerRecords from topic {}", consumerRecords.count(), topic);

                Map<TopicPartition, Long> lastOffsets = new HashMap<>();
                List<ConsumerRecord<Integer, String>> failedRecords = new ArrayList<>();

                for (ConsumerRecord<Integer, String> consumerRecord : consumerRecords) {
                    processRecord(consumerRecord, failedRecords, lastOffsets);
                }

                failedRecords.forEach(this::pushToRetryQueue);

                commitOffsets(consumer, lastOffsets);
            }
        } catch (Exception ex) {
            log.error("Error in Kafka consumer loop", ex);
        } finally {
            consumer.close();
            log.info("Kafka consumer closed");
        }
    }


    public List<TestEvent> pullBatch(int maxMessages) {
        List<TestEvent> testEvents = new ArrayList<>(maxMessages);
        eventQueue.drainTo(testEvents, maxMessages);
        log.info("Pulled batch of {} events from queue", testEvents.size());
        return testEvents;
    }

    public int estimateRemaining() {
        return eventQueue.size();
    }

    private KafkaConsumer<Integer, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    private List<TopicPartition> getTopicPartitions(KafkaConsumer<Integer, String> consumer) {
        return consumer.partitionsFor(topic).stream()
                       .map(info -> new TopicPartition(topic, info.partition()))
                       .toList();
    }

    private TestEvent parseRecord(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            return kafkaRecordReader.readConsumerRecord(consumerRecord, TestEvent.class);
        } catch (KafkaDeserializationException | KafkaValidationException exception) {
            throw new UnexpectedException(ErrorCode.ONBOARDING_UNEXPECTED_001, "Unable to read consumerRecord", exception);
        }
    }

    private void commitOffsets(KafkaConsumer<Integer, String> consumer, Map<TopicPartition, Long> offsets) {
        if (offsets.isEmpty())
            return;

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        offsets.forEach((partition, offset) -> offsetsToCommit.put(partition, new OffsetAndMetadata(offset)));

        consumer.commitSync(offsetsToCommit);
        log.debug("Committed offsets: {}", offsetsToCommit);
    }
}
