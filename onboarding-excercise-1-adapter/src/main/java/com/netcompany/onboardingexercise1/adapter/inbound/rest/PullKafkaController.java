package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.netcompany.onboardingexercise1.event.testevent.TestEvent;
import com.netcompany.onboardingexercise1.rest.dto.BatchConsumption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/batches")
@RequiredArgsConstructor
@Slf4j
public class PullKafkaController {

    private final BatchTestEventService pullKafkaService;

    @Value("${pull.default.max:10}")
    private int defaultMax;

    @GetMapping("/test-events")
    public ResponseEntity<BatchConsumption<TestEvent>> pullEvents(@RequestParam(value = "max", required = false) Integer max) {
        int batchSize = (max != null) ? max : defaultMax;

        List<TestEvent> batch = pullKafkaService.pullBatch(batchSize);
        int messagesLeft = pullKafkaService.estimateRemaining();
        boolean hasMore = messagesLeft > 0;

        BatchConsumption<TestEvent> consumption = BatchConsumption.<TestEvent>builder().eventsBatch(batch).hasMore(hasMore).messagesLeft(messagesLeft).build();

        return ResponseEntity.ok(consumption);
    }
}
