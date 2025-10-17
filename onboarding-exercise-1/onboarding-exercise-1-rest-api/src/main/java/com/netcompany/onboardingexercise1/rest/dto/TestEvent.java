package com.netcompany.onboardingexercise1.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestEvent {
    @NotNull
    private Long id;

    @NotNull
    private String message;
}
