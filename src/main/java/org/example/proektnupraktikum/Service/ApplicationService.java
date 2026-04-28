package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Request.ReviewRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.BadRequestException;
import org.example.proektnupraktikum.Exception.ConflictException;
import org.example.proektnupraktikum.Exception.ForbiddenException;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Service.Mapper.ApplicationMapper;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentRepository;
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    public ApplicationResponse apply(ApplicationRequest request) {
        StudentProfile student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException(
                        "Student with id %d not found".formatted(request.getStudentId())));

        Vacancy vacancy = vacancyRepository.findById(request.getVacancyId())
                .orElseThrow(() -> new NotFoundException(
                        "Vacancy with id %d not found".formatted(request.getVacancyId())));

        Application application = new Application();
        application.setStudent(student);
        application.setVacancy(vacancy);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> findApplicationsById(Long studentId, Long employerId) {
        List<Application> applications = studentId != null
                ? applicationRepository.findApplicationsByStudentId(studentId)
                : applicationRepository.findApplicationsByVacancyEmployerId(employerId);

        return applications.stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public ApplicationResponse getApplicationById(Long id, String userEmail) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        return applicationMapper.toResponse(application);
    }

    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String userEmail) {
        if (status == null || status == ApplicationStatus.PENDING) {
            throw new BadRequestException("Status must be ACCEPTED or REJECTED");
        }

        User user = findEmployerUser(userEmail);
        Application application = findApplication(applicationId);
        validateOwnership(application, user);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ConflictException("Only PENDING applications can be updated");
        }

        application.setStatus(status);
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    public ApplicationResponse leaveReview(Long applicationId, ReviewRequest request, String userEmail) {
        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("Comment must not be empty");
        }

        User user = findEmployerUser(userEmail);
        Application application = findApplication(applicationId);
        validateOwnership(application, user);

        Review review = resolveReview(application);
        review.setComment(request.getComment().trim());
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        application.setReview(review);
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    private User findEmployerUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new ForbiddenException("Only employer can perform this action");
        }
        return user;
    }

    private Application findApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private void validateOwnership(Application application, User user) {
        Long ownerId = application.getVacancy().getEmployer().getUser().getId();
        if (!ownerId.equals(user.getId())) {
            throw new ForbiddenException("You can only manage applications for your own vacancies");
        }
    }

    private Review resolveReview(Application application) {
        Review review = application.getReview();
        if (review != null) return review;

        Review newReview = new Review();
        newReview.setApplication(application);
        newReview.setEmployer(application.getVacancy().getEmployer());
        newReview.setStudent(application.getStudent());
        return newReview;
    }
}
