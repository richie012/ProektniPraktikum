package org.example.proektnupraktikum.Repository;

import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByUser(User user);

    Employer findEmployerById(Long id);

    Employer findEmployerByUserId(Long id);
}

