package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentRepository;
    private final VacancyRepository vacancyRepository;

    public Application apply(ApplicationRequest request) {

        StudentProfile student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student with id %d not found".formatted(request.getStudentId())));

        Vacancy vacancy = vacancyRepository.findById(request.getVacancyId())
                .orElseThrow(() -> new RuntimeException("Vacancy with id %d not found".formatted(request.getVacancyId())));

        Application application = new Application();
        application.setStudent(student);
        application.setVacancy(vacancy);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public List<ApplicationResponse> findApplicationsById(Long studentId, Long employerId) {
        List<Application> applications;

        if (studentId != null) {
            applications = applicationRepository.findApplicationsByStudentId(studentId);
        } else {
            applications = applicationRepository.findApplicationsByVacancyEmployerId(employerId);
        }

        return applications
                .stream()
                .map(application -> {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setId(application.getId());
                    response.setStudentId(application.getStudent().getId());
                    response.setVacancyId(application.getVacancy().getId());
                    response.setCreatedAt(application.getCreatedAt());
                    response.setStatus(application.getStatus().name());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
