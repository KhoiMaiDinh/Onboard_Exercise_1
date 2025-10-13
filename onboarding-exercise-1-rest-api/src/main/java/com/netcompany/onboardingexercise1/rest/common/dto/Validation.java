package com.netcompany.onboardingexercise1.rest.common.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Validation {

    private String field;

    private String message;
}