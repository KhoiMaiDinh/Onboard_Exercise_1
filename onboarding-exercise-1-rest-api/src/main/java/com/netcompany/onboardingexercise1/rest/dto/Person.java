package com.netcompany.onboardingexercise1.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcompany.onboardingexercise1.shared.dto.Create;
import com.netcompany.onboardingexercise1.shared.dto.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;


@Data
public class Person {

    @Null(groups = Create.class, message = "ID must be null for new persons")
    @NotNull(groups = Update.class, message = "ID is required for updates")
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(groups = Create.class, message = "Tax number is required")
    @Size(max = 32)
    private String taxNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer age;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long taxDebt;
}
