package com.netcompany.onboardingexercise1.adapter.mapper;

import com.netcompany.onboardingexercise1.rest.dto.PersonFilterRequest;
import org.mapstruct.Mapper;
import com.netcompany.onboardingexercise1.core.domain.dto.PersonFilter;

@Mapper
public interface PersonFilterMapper {

    PersonFilter toPersonFilter(PersonFilterRequest personFilterRequest);
}
