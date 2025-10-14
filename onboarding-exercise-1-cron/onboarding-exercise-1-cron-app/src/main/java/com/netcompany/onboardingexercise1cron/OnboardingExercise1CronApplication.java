package com.netcompany.onboardingexercise1cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages= {"com.netcompany.onboardingexercise1cron"})
@EnableScheduling
public class OnboardingExercise1CronApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnboardingExercise1CronApplication.class, args);
    }
}
