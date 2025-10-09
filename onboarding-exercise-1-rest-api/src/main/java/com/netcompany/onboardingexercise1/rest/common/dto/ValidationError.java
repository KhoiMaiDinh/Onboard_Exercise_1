package com.netcompany.onboardingexercise1.rest.common.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private String field;
    private String message;
}