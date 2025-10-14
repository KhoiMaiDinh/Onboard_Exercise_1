package com.netcompany.onboardingexercise1.event.testevent;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Data
public class TestEvent {
    @NotNull
    private Long id;

    private String message;
}
