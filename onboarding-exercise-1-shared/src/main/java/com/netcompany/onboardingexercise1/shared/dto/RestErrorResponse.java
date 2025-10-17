package com.netcompany.onboardingexercise1.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestErrorResponse {

    private Instant timestamp;

    private int status;

    private String error;
    
    private String message;

    private String path;

    private List<Validation> violations;

    private String stackTrace;
}