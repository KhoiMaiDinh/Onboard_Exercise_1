package com.netcompany.onboardingexercise1.core.service.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.enums.PersonEventType;
import com.netcompany.onboardingexercise1.core.domain.event.PersonEventDomain;
import com.netcompany.onboardingexercise1.core.port.outbound.kafka.PersonEventPort;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class PersonEventServiceImpl implements PersonEventService {

    private final PersonEventPort personEventPort;

    public PersonEventServiceImpl(PersonEventPort personEventPort) {
        this.personEventPort = personEventPort;
    }


    @Override
    public void create(PersonDomain personDomain) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        PersonEventDomain personEventDomainCreate = new PersonEventDomain();

        personEventDomainCreate.setPersonEventType(PersonEventType.CREATE);
        personEventDomainCreate.setPersonDomain(personDomain);

        personEventPort.produce(personEventDomainCreate);
    }

    @Override
    public void update(Long id, PersonDomain personDomain) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        personDomain.setId(id);
        PersonEventDomain personEventDomainUpdate = new PersonEventDomain();

        personEventDomainUpdate.setPersonEventType(PersonEventType.UPDATE);
        personEventDomainUpdate.setPersonDomain(personDomain);


        personEventPort.produce(personEventDomainUpdate);
    }
}
