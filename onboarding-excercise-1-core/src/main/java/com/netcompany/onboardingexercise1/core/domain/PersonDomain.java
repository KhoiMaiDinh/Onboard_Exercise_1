package com.netcompany.onboardingexercise1.core.domain;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PersonDomain {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String taxNumber;

    private Instant createdAt;
}
