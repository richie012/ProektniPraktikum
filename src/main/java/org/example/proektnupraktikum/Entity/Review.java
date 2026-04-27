package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employer employer;

    @ManyToOne
    private StudentProfile student;

    private Integer rating;

    private String comment;

}
