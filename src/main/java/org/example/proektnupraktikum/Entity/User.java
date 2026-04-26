package org.example.proektnupraktikum.Entity;

import jakarta.persistence.*;
import org.example.proektnupraktikum.Entity.Enum.Role;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // STUDENT, EMPLOYER

}