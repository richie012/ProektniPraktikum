package org.example.proektnupraktikum.repository;

import org.example.proektnupraktikum.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
