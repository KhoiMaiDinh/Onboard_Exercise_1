package com.netcompany.onboardingexercise1.adapter.mapper;

import com.netcompany.onboardingexercise1.rest.dto.TestEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestEventMapper {

    TestEvent toRestDto(com.netcompany.onboardingexercise1.event.testevent.TestEvent testEvent);

    com.netcompany.onboardingexercise1.event.testevent.TestEvent toEventDto(TestEvent testEvent);
}
