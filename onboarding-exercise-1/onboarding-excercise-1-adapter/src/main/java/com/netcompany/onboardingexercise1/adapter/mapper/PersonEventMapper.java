package com.netcompany.onboardingexercise1.adapter.mapper;

import com.netcompany.onboardingexercise1.core.domain.event.PersonEventDomain;
import com.netcompany.onboardingexercise1.event.personevent.PersonEvent;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PersonEventMapper {
    @Mapping(source = "personEventTypeDomain", target = "personEventType")
    @Mapping(source = "personDomain", target = "person")
    PersonEvent toDto(PersonEventDomain personEventDomain);

    @InheritInverseConfiguration
    PersonEventDomain toDomain(PersonEvent event);
}
