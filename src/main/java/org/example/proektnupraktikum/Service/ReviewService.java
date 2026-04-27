package org.example.proektnupraktikum.Service;

import lombok.AllArgsConstructor;
import org.example.proektnupraktikum.Dto.Review.ReviewPostRequestDto;
import org.example.proektnupraktikum.Dto.Review.ReviewPostResponseDto;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.ReviewRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final EmployerRepository employerRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public ReviewPostResponseDto createReview(ReviewPostRequestDto dto, String employerEmail) {
        Application application = applicationRepository.findApplicationById(dto.getApplicationId());
        if (application == null) {
            throw new IllegalArgumentException("Application not found with id: " + dto.getApplicationId());
        }
        User user = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can leave review");
        }
        Employer employer = employerRepository.findEmployerByUserId(user.getId());
        if (employer == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Employer not found for user");
        }
        if (!application.getVacancy().getEmployer().getId().equals(employer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can review only applications for your vacancies");
        }
        StudentProfile studentProfile = studentProfileRepository.findStudentProfileById(application.getStudent().getId());
        if (studentProfile == null) {
            throw new IllegalArgumentException("StudentProfile not found with id: " + application.getStudent().getId());
        }
        Review review = new Review();
        review.setApplication(application);
        review.setEmployer(employer);
        review.setRating(dto.getRating());
        review.setStudent(studentProfile);
        review.setComment(dto.getComment());
        Review createdReview = reviewRepository.save(review);

        application.setStatus(ApplicationStatus.CLOSED);
        application.setReview(createdReview);
        applicationRepository.save(application);


        return new ReviewPostResponseDto(
                createdReview.getId(),
                createdReview.getComment(),
                createdReview.getRating(),
                createdReview.getEmployer().getId(),
                createdReview.getStudent().getId(),
                createdReview.getApplication().getId()
        );
    }
}
