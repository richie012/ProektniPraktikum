package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToOne
    private Application application;

}
