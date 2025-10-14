package com.netcompany.onboardingexercise1.adapter.outbound.persistense.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERSON")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSON_ID", nullable = false)
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "DATE_OF_BIRTH_DT", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "TAX_NUMBER", nullable = false)
    private String taxNumber;

    @Column(name = "TAX_DEBT", nullable = false)
    private BigDecimal taxDebt;

    @Column(name = "CREATED_DTTM", nullable = false)
    private Instant createdAt;

    @Column(name = "DELETED_DTTM")
    private Instant deletedAt;
}
