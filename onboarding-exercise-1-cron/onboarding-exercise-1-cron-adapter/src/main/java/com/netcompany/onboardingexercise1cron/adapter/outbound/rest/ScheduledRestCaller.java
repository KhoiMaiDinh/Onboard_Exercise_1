package com.netcompany.onboardingexercise1cron.adapter.outbound.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ScheduledRestCaller {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final int CALL_RATE = 1000;

    private final String targetUrl;

    public ScheduledRestCaller(@Value("${onboarding-exercise-1-cron.rest.url.onboarding-exercise-1}/api/v1/batches/test-events") String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Scheduled(fixedRate = CALL_RATE)
    public void callRestEndpoint() {
        try {
            String response = restTemplate.getForObject(targetUrl, String.class);
            log.info("REST API RESPONSE: {}", response);
        } catch (Exception e) {
            log.error("REST API ERROR: {}", e.getMessage());
        }
    }
}
