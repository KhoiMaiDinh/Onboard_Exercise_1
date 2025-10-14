package com.netcompany.onboardingexercise1.adapter.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("dev")
public class KafkaAutoCreateConfig {

    @Value("${onboarding-exercise-1.kafka.inbound.person-topic}")
    public String personTopic;

    @Value("${onboarding-exercise-1.kafka.inbound.tax-calculation-topic}")
    public String taxCalculationTopic;

    @Value("${onboarding-exercise-1.kafka.inbound.test-topic}")
    public String testTopic;


    @Bean
    public NewTopic personEvent(){
        return TopicBuilder.name(personTopic)
                           .partitions(1)
                           .replicas(1)
                           .build();
    }

    @Bean
    public NewTopic taxCalculationEvent(){
        return TopicBuilder.name(taxCalculationTopic)
                           .partitions(1)
                           .replicas(1)
                           .build();
    }

    @Bean
    public NewTopic testEvent(){
        return TopicBuilder.name(testTopic)
                           .partitions(3)
                           .replicas(1)
                           .build();
    }
}