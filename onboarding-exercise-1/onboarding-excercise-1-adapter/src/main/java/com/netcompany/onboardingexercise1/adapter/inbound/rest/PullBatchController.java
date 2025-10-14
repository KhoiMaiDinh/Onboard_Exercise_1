package com.netcompany.onboardingexercise1.adapter.inbound.rest;

import com.netcompany.onboardingexercise1.adapter.inbound.kafka.BatchTestEventService;
import com.netcompany.onboardingexercise1.adapter.mapper.TestEventMapper;
import com.netcompany.onboardingexercise1.rest.api.PullBatchApi;
import com.netcompany.onboardingexercise1.rest.dto.BatchConsumption;
import com.netcompany.onboardingexercise1.rest.dto.TestEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/batches")
@RequiredArgsConstructor
public class PullBatchController implements PullBatchApi {

    private final BatchTestEventService pullKafkaService;

    private final TestEventMapper testEventMapper;

    @Value("${pull.default.max:10}")
    private int defaultMax;


    public ResponseEntity<BatchConsumption<TestEvent>> pullEvents(Integer max) {
        int batchSize = (max != null) ? max : defaultMax;

        List<TestEvent> batch = pullKafkaService.pullBatch(batchSize).stream().map(testEventMapper::toRestDto).toList();
        int messagesLeft = pullKafkaService.estimateRemaining();
        boolean hasMore = messagesLeft > 0;

        BatchConsumption<TestEvent> consumption = BatchConsumption.<TestEvent>builder().eventsBatch(batch).hasMore(hasMore).messagesLeft(messagesLeft).build();

        return ResponseEntity.ok(consumption);
    }
}
