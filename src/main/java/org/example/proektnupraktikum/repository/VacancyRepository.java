package org.example.proektnupraktikum.repository;

import org.example.proektnupraktikum.Entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
}