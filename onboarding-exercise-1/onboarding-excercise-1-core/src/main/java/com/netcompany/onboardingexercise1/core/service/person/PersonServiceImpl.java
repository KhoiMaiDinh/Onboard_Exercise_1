package com.netcompany.onboardingexercise1.core.service.person;

import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.dto.PersonFilter;
import com.netcompany.onboardingexercise1.core.port.outbound.persistence.PersonPort;
import com.netcompany.onboardingexercise1.shared.exception.DuplicationException;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonPort personPort;

    public PersonServiceImpl(PersonPort personPort) {
        this.personPort = personPort;
    }

    @Override
    public PersonDomain save(PersonDomain personDomain) {
        if (personPort.existByTaxNumber(personDomain.getTaxNumber()))
            throw new DuplicationException("Person", "Tax number", personDomain.getTaxNumber());

        return personPort.save(personDomain);
    }

    @Override
    public PersonDomain findById(Long id) {
        return personPort.findById(id);
    }

    @Override
    public PersonDomain findByTaxNumber(String taxNumber) {
        return personPort.findByTaxNumber(taxNumber);
    }

    @Override
    public Page<PersonDomain> find(PersonFilter personFilter) {
        return personPort.find(personFilter);
    }

    @Override
    public PersonDomain update(Long id, PersonDomain personDomain) {
        PersonDomain existingPerson = findById(personDomain.getId());

        if (!existingPerson.getTaxNumber().equals(personDomain.getTaxNumber())) {
            throw new IllegalArgumentException("Tax number can not be change");
        }

        personDomain.setId(id);
        return personPort.update(personDomain);
    }

    @Override
    public void handleTaxCalculation(String taxNumber, BigDecimal taxAmount) {
        PersonDomain personDomain = findByTaxNumber(taxNumber);
        personDomain.addTaxDebt(taxAmount);
        update(personDomain.getId(), personDomain);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        personPort.delete(id);
    }

}
