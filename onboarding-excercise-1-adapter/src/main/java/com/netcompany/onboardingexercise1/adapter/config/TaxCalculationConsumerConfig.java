package com.netcompany.onboardingexercise1.adapter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@Slf4j
public class TaxCalculationConsumerConfig extends AbstractKafkaConsumerConfig {

    public TaxCalculationConsumerConfig(@Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}") String topic, KafkaTemplate<Object, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(topic, kafkaTemplate, kafkaProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "taxCalculationKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<Object, Object> taxCalculationKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory,
                kafkaConsumerFactory.getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(mainErrorHandler());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(name = "taxCalculationRetryKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> taxCalculationRetryKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory,
                kafkaConsumerFactory.getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(retryErrorHandler());
        return factory;
    }

}
