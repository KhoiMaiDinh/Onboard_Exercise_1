package com.netcompany.onboardingexercise1.adapter.outbound.persistense;

import com.netcompany.onboardingexercise1.adapter.mapper.PersonMapper;
import com.netcompany.onboardingexercise1.adapter.outbound.persistense.entities.PersonEntity;
import com.netcompany.onboardingexercise1.adapter.outbound.persistense.repository.PersonRepository;
import com.netcompany.onboardingexercise1.adapter.outbound.persistense.repository.specification.PersonSpecifications;
import com.netcompany.onboardingexercise1.core.domain.PersonDomain;
import com.netcompany.onboardingexercise1.core.domain.dto.PersonFilter;
import com.netcompany.onboardingexercise1.core.exception.NotFoundException;
import com.netcompany.onboardingexercise1.core.port.outbound.persistence.PersonPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class PersonDBAdapter implements PersonPort {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    public PersonDBAdapter(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    @Override
    public PersonDomain save(PersonDomain personDomain) {
        PersonEntity personEntitySaved = personRepository.save(personMapper.domainToEntity(personDomain));

        return personMapper.entityToDomain(personRepository.save(personEntitySaved));
    }

    @Override
    public PersonDomain update(PersonDomain personDomain) {
        PersonEntity personEntityUpdated = personRepository.save(personMapper.domainToEntity(personDomain));

        return personMapper.entityToDomain(personRepository.save(personEntityUpdated));
    }

    @Override
    public PersonDomain findById(Long id) {
        return personRepository.findByIdAndDeletedAtIsNull(id).map(personMapper::entityToDomain).orElseThrow(() -> new NotFoundException("id", id));
    }

    @Override
    public PersonDomain findByTaxNumber(String taxNumber) {
        return personRepository.findByTaxNumber(taxNumber).map(personMapper::entityToDomain).orElseThrow(() -> new NotFoundException("taxNumber", taxNumber));
    }

    @Override
    public Page<PersonDomain> find(PersonFilter personFilter) {
        Specification<PersonEntity> personSpecification = PersonSpecifications.notDeleted();

        if (personFilter.getMinAge() != null) {
            personSpecification = personSpecification.and(PersonSpecifications.ageGreaterThan(personFilter.getMinAge()));
        }

        if (personFilter.getQ() != null && !personFilter.getQ().isEmpty()) {
            Specification<PersonEntity> nameSpecification =
                    PersonSpecifications.firstNameLike(personFilter.getQ()).or(PersonSpecifications.lastNameLike(personFilter.getQ()));

            personSpecification = personSpecification.and(nameSpecification);
        }
        Pageable pageable = PageRequest.of(personFilter.getPage(), personFilter.getSize(),
                Sort.by(Sort.Direction.fromString(personFilter.getSortDirection()), personFilter.getSortBy()));

        return personRepository.findAll(personSpecification, pageable).map(personMapper::entityToDomain);
    }

    @Override
    public void delete(Long id) {
        personRepository.softDeleteById(id);
    }

    @Override
    public boolean existByTaxNumber(String taxNumber) {
        return personRepository.existsByTaxNumber(taxNumber);
    }

}
