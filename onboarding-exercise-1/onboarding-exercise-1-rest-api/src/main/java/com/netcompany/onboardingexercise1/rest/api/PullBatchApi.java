package com.netcompany.onboardingexercise1.rest.api;

import com.netcompany.onboardingexercise1.rest.dto.BatchConsumption;
import com.netcompany.onboardingexercise1.rest.dto.TestEvent;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface PullBatchApi {

    @GetMapping("/test-events")
    ResponseEntity<BatchConsumption<TestEvent>> pullEvents(
            @Min(1) @RequestParam(value = "max", required = false) Integer max
    );
}