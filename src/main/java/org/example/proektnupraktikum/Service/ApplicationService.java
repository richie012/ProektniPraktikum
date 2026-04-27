package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Dto.ReviewDto;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentRepository;
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;

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
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String userEmail) {
        if (status == null || status == ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be ACCEPTED or REJECTED");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getRole() != Role.EMPLOYER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can update application status");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        Long applicationEmployerUserId = application.getVacancy().getEmployer().getUser().getId();
        if (!applicationEmployerUserId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can update only applications for your vacancies");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only PENDING applications can be updated");
        }

        application.setStatus(status);
        Application updated = applicationRepository.save(application);
        return toResponse(updated);
    }

    private ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStudentId(application.getStudent().getId());
        response.setStudentName(application.getStudent().getName());
        response.setStudentPhone(application.getStudent().getPhone());
        response.setStudentSkills(application.getStudent().getSkills());
        response.setStudentEmail(
                application.getStudent().getUser() != null
                        ? application.getStudent().getUser().getEmail()
                        : null
        );
        response.setVacancyId(application.getVacancy().getId());
        response.setCreatedAt(application.getCreatedAt());
        response.setStatus(application.getStatus().name());
        if (application.getReview() != null) {
            Review review = application.getReview();
            ReviewDto dto = new ReviewDto();
            dto.setId(review.getId());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setEmployerId(review.getEmployer() != null ? review.getEmployer().getId() : null);
            dto.setStudentId(review.getStudent() != null ? review.getStudent().getId() : null);
            response.setReview(dto);
        }
        return response;
    }
}
