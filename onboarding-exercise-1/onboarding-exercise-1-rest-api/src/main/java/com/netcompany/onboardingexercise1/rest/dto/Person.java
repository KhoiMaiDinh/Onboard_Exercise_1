package com.netcompany.onboardingexercise1.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcompany.onboardingexercise1.shared.dto.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;


@Data
public class Person {

    @Null(groups = Create.class, message = "ID must be null for new persons")
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(groups = Create.class, message = "Tax number is required")
    @Size(max = 32)
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Tax number must contain only uppercase letters and numbers")
    private String taxNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer age;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long taxDebt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;
}
