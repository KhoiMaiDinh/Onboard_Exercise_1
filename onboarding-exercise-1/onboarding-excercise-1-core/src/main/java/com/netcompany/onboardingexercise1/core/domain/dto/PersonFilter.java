package com.netcompany.onboardingexercise1.core.domain.dto;

import lombok.Data;

@Data
public class PersonFilter {

    private final String q;

    private final Integer minAge;

    private final int page;

    private final int size;

    private final String sortBy;

    private final String sortDirection;
}