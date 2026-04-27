package org.example.proektnupraktikum.Repository;

import org.example.proektnupraktikum.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Application findApplicationById(Long id);

    List<Application> findApplicationsByStudentId(Long studentId);

    List<Application> findApplicationsByVacancyEmployerId(Long employerId);
}
