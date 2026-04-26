package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    private Employer employer;

    private LocalDateTime createdAt;

}
