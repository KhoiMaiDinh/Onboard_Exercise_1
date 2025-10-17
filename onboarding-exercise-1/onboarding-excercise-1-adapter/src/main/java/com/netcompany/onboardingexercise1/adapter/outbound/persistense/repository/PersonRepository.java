package com.netcompany.onboardingexercise1.adapter.outbound.persistense.repository;

import com.netcompany.onboardingexercise1.adapter.outbound.persistense.entities.PersonEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long>, JpaSpecificationExecutor<PersonEntity> {
    @Transactional
    @Modifying
    @Query("UPDATE PersonEntity p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    void softDeleteById(Long id);

    @Query("SELECT p FROM PersonEntity p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<PersonEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT p FROM PersonEntity p WHERE p.taxNumber = :taxNumber AND p.deletedAt IS NULL")
    Optional<PersonEntity> findByTaxNumberAndDeletedAtIsNull(String taxNumber);

    Boolean existsByTaxNumber(String taxNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PersonEntity p WHERE p.taxNumber = :taxNumber AND p.deletedAt IS NULL")
    Optional<PersonEntity> findAndLockByTaxNumber(String taxNumber);
}
