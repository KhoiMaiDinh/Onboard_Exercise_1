package com.netcompany.onboardingexercise1.core.service.person;

import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.dto.PersonFilter;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;

public interface PersonService {
    PersonDomain save(PersonDomain personDomain);

    PersonDomain findById(Long id);

    PersonDomain findByTaxNumber(String taxNumber);

    Page<PersonDomain> find(PersonFilter personFilter);

    PersonDomain update(Long id, PersonDomain personDomain);

    void handleTaxCalculation(String taxNumber, BigDecimal taxAmount);

    void delete(Long id);
}