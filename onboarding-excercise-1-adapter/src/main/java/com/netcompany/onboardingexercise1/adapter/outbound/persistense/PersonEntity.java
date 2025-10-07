package com.netcompany.onboardingexercise1.adapter.outbound.persistense;

import jakarta.persistence.*;

@Entity
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSON_ID", nullable = false)
    private Long id;
}
