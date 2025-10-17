package com.netcompany.onboardingexercise1.core.port.outbound.persistence;

import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.dto.PersonFilter;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;

public interface PersonPort {

    PersonDomain save(PersonDomain personDomain);

    PersonDomain update(PersonDomain personDomain);

    PersonDomain findById(Long id);

    void handleTaxCalculationAtomic(String taxNumber, BigDecimal amount);

    PersonDomain findByTaxNumber(String taxNumber);

    Page<PersonDomain> find(PersonFilter personFilter);

    void delete(Long id);

    boolean existByTaxNumber(String taxNumber);

}
