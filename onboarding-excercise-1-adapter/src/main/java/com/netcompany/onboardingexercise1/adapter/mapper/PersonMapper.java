package com.netcompany.onboardingexercise1.adapter.mapper;

import com.netcompany.onboardingexercise1.adapter.outbound.persistense.entities.PersonEntity;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.rest.dto.Person;
import java.time.LocalDate;
import java.time.Period;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDomain dtoToDomain(Person person);

    @Mapping(target = "age", expression = "java(calculateAge(personDomain.getDateOfBirth()))")
    Person domainToDto(PersonDomain personDomain);

    PersonDomain entityToDomain(PersonEntity personEntity);

    PersonEntity domainToEntity(PersonDomain personDomain);

    default Integer calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
