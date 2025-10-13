package com.netcompany.onboardingexercise1.rest.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventErrorResponse {

    private Instant timestamp;

    private String topic;

    private Integer partition;

    private Long offset;

    private String key;

    private String originalPayload;

    private String exceptionType;

    private String message;

    private String stackTrace;

    private List<Validation> violations;
}
