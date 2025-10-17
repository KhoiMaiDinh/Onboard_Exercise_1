package com.netcompany.onboardingexercise1.event.personevent;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Person {
    private Long id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private LocalDate dateOfBirth;

    @NotEmpty
    private String taxNumber;

    private BigDecimal taxDebt;

    private Instant createdAt;


}
