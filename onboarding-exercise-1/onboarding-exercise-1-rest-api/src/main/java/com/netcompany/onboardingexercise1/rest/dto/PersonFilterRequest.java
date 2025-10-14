package com.netcompany.onboardingexercise1.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PersonFilterRequest {

    private String q;

    @Min(value = 0, message = "Minimum age must be 0 or greater")
    private Integer minAge;

    @Min(value = 0, message = "Page index must be 0 or greater")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 10;

    @Pattern(
            regexp = "firstName|lastName|dateOfBirth|taxNumber",
            message = "Invalid sortBy field"
    )
    private String sortBy = "lastName";

    @Pattern(regexp = "ASC|DESC", message = "Sort direction must be ASC or DESC")
    private String sortDirection = "ASC";
}
