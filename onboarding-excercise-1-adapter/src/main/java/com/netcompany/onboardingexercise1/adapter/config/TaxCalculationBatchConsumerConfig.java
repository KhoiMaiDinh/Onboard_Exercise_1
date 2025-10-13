package com.netcompany.onboardingexercise1.adapter.config;

import java.util.HashMap;
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

    protected TaxCalculationBatchConsumerConfig(@Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}") String topic, KafkaTemplate<Object, Object> kafkaTemplate,
            KafkaProperties kafkaProperties) {
        super(topic, kafkaTemplate, kafkaProperties);
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "onboarding-excercise-1-listener-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }


    @Bean
    @ConditionalOnMissingBean(name = "taxCalculationNonBlockingBatchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> taxCalculationNonBlockingBatchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setBatchListener(true);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);


        factory.setCommonErrorHandler(mainErrorHandler());

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
