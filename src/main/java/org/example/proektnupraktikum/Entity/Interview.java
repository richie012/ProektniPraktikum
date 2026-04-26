package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Application application;

    private LocalDateTime date;

    private String location;

}
