package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;

@Entity
public class CoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Application application;

    @Column(length = 2000)
    private String text;

}
