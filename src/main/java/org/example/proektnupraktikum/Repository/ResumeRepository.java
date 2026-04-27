package org.example.proektnupraktikum.Repository;

import org.example.proektnupraktikum.Entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByStudentId(Long studentId);
}
