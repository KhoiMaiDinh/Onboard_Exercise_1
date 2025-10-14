package com.netcompany.onboardingexercise1.adapter.config;


import com.netcompany.onboardingexercise1.shared.exception.NotFoundException;
import com.netcompany.onboardingexercise1.shared.kafka.config.AbstractKafkaConsumerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class PersonEventsConsumerConfig extends AbstractKafkaConsumerConfig {

    protected PersonEventsConsumerConfig(@Value("${onboarding-exercise-1.kafka.inbound.person-topic}") String topic, KafkaTemplate<Object, Object> kafkaTemplate,
            KafkaProperties kafkaProperties) {
        super(topic, kafkaTemplate, kafkaProperties);
    }

    @Override
    protected List<Class<? extends Exception>> getNonRetryableExceptions() {
        List<Class<? extends Exception>> parentList = super.getNonRetryableExceptions();
        List<Class<? extends Exception>> extendedList = new ArrayList<>(parentList);
        extendedList.add(NotFoundException.class);

        return Collections.unmodifiableList(extendedList);
    }

    @Bean
    @ConditionalOnMissingBean(name = "personKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> personKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory,
                kafkaConsumerFactory.getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(retryErrorHandler());
        return factory;
    }
}
