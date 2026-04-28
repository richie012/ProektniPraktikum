package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Entity.Resume;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Repository.ResumeRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final StudentProfileRepository studentProfileRepository;

    public Optional<Resume> findByStudentId(Long studentId) {
        return resumeRepository.findByStudentId(studentId);
    }

    public Resume save(Long studentId, String fileUrl) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Resume resume = resumeRepository.findByStudentId(studentId)
                .orElse(new Resume());

        resume.setStudent(student);
        resume.setFileUrl(fileUrl);

        return resumeRepository.save(resume);
    }
}