package org.example.proektnupraktikum.repository;

import org.example.proektnupraktikum.Entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}
