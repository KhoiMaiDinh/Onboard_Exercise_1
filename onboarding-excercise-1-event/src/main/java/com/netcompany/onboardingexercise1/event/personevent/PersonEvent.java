package com.netcompany.onboardingexercise1.event.personevent;


import com.netcompany.onboardingexercise1.rest.dto.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersonEvent {
    private PersonEventType personEventType;

    private Person person;
}
