package com.netcompany.onboardingexercise1.core.domain.event;

import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.enums.PersonEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersonEventDomain {
    private PersonEventType personEventType;

    private PersonDomain personDomain;
}
