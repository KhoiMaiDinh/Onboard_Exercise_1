package com.netcompany.onboardingexercise1.adapter.config;

import com.netcompany.onboardingexercise1.shared.kafka.config.AbstractKafkaConsumerConfig;
import jakarta.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@EnableKafka
public class TaxCalculationBatchConsumerConfig extends AbstractKafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    protected TaxCalculationBatchConsumerConfig(@Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}") String topic,
            KafkaTemplate<Object, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(topic, kafkaTemplate, kafkaProperties);
    }


    public Map<String, Object> consumerConfigs(String bootstrapServers, String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(bootstrapServers, groupId));
    }

    @Override
    protected List<Class<? extends Exception>> getNonRetryableExceptions() {
        List<Class<? extends Exception>> parentList = super.getNonRetryableExceptions();
        List<Class<? extends Exception>> extendedList = new ArrayList<>(parentList);
        extendedList.add(OptimisticLockException.class);

        return Collections.unmodifiableList(extendedList);
    }


    @Bean
    @ConditionalOnMissingBean(name = "taxCalculationNonBlockingBatchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> taxCalculationNonBlockingBatchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setBatchListener(true);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);


        factory.setCommonErrorHandler(mainErrorHandler(100L, 10L));

        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "taxCalculationBlockingBatchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> taxCalculationBlockingBatchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setBatchListener(true);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);


        factory.setCommonErrorHandler(retryErrorHandler());

        return factory;
    }
}
