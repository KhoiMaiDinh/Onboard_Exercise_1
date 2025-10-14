package com.netcompany.onboardingexercise1.adapter.outbound.persistense.repository.specification;

import com.netcompany.onboardingexercise1.adapter.outbound.persistense.entities.PersonEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecifications {

    private PersonSpecifications() {}

    public static Specification<PersonEntity> ageGreaterThan(int age) {
        return (root, query, cb) -> {
            LocalDate cutoffDate = LocalDate.now().minusYears(age);
            return cb.lessThan(root.get("dateOfBirth"), cutoffDate);
        };
    }

    public static Specification<PersonEntity> firstNameLike(String namePattern) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + namePattern.toLowerCase() + "%");
    }

    public static Specification<PersonEntity> lastNameLike(String namePattern) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + namePattern.toLowerCase() + "%");
    }

    public static Specification<PersonEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }
}
