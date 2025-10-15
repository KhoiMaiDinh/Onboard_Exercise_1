package com.netcompany.onboardingexercise1.adapter.inbound.kafka;

import com.netcompany.onboardingexercise1.event.testevent.TestEvent;
import com.netcompany.onboardingexercise1.shared.enums.ErrorCode;
import com.netcompany.onboardingexercise1.shared.exception.KafkaDeserializationException;
import com.netcompany.onboardingexercise1.shared.exception.KafkaValidationException;
import com.netcompany.onboardingexercise1.shared.exception.UnexpectedException;
import com.netcompany.onboardingexercise1.shared.utils.KafkaRecordReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BatchTestEventService {

    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private static final Integer MAX_POLL_RECORDS = 100;

    private final String topic;

    private final String bootstrapServers;


    private final String consumerGroupId;

    private final KafkaRecordReader kafkaRecordReader;


    public BatchTestEventService(@Value("${onboarding-exercise-1.kafka.inbound.test-topic}") String topic,
            @Value("${spring.kafka.consumer.bootstrap-servers}") String bootstrapServers,
            @Value("${onboarding-exercise-1.kafka.manual-consumer-group-id}") String consumerGroupId, KafkaRecordReader kafkaRecordReader) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.consumerGroupId = consumerGroupId;
        this.kafkaRecordReader = kafkaRecordReader;
    }

    public List<TestEvent> pullBatch(int maxMessages) {
        List<TestEvent> testEvents = new ArrayList<>();

        KafkaConsumer<Integer, String> consumer = createConsumer();
        List<TopicPartition> partitions = getTopicPartitions(consumer);
        consumer.assign(partitions);

        ConsumerRecords<Integer, String> consumerRecords = consumer.poll(POLL_TIMEOUT);
        log.info("Polled {} consumerRecords from topic {}", consumerRecords.count(), topic);

        Map<TopicPartition, Long> lastOffsets = new HashMap<>();
        int count = 0;

        for (ConsumerRecord<Integer, String> consumerRecord : consumerRecords) {
            if (count >= maxMessages)
                break;

            TestEvent event = parseRecord(consumerRecord);
            if (event != null) {
                testEvents.add(event);
                lastOffsets.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()), consumerRecord.offset() + 1);
                count++;
            }
        }

        commitOffsets(consumer, lastOffsets);

        return testEvents;
    }

    public int estimateRemaining() {
        int remainingMessages = 0;

        KafkaConsumer<Integer, String> consumer = createConsumer();
        List<TopicPartition> partitions = getTopicPartitions(consumer);
        consumer.assign(partitions);

        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);

        for (TopicPartition partition : partitions) {
            long position = consumer.position(partition);
            long end = endOffsets.getOrDefault(partition, 0L);
            remainingMessages += (int) Math.max(end - position, 0);
        }

        return remainingMessages;
    }

    private KafkaConsumer<Integer, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);
        return new KafkaConsumer<>(props);
    }

    private List<TopicPartition> getTopicPartitions(KafkaConsumer<Integer, String> consumer) {
        return consumer.partitionsFor(topic).stream().map(info -> new TopicPartition(topic, info.partition())).toList();
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
        log.info("Committed offsets: {}", offsetsToCommit);
    }
}