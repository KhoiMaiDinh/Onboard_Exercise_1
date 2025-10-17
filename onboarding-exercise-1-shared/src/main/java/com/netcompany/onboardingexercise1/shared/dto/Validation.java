package com.netcompany.onboardingexercise1.shared.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Validation {

    private String field;

    private String message;
}