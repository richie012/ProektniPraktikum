package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Review.ReviewPostRequestDto;
import org.example.proektnupraktikum.Dto.Review.ReviewPostResponseDto;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Exception.ForbiddenException;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Exception.UnauthorizedException;
import org.example.proektnupraktikum.Service.Mapper.ReviewMapper;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.ReviewRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final EmployerRepository employerRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewPostResponseDto createReview(ReviewPostRequestDto dto, String employerEmail) {
        Application application = findApplication(dto.getApplicationId());
        User user = findEmployerUser(employerEmail);
        Employer employer = findEmployer(user);

        validateOwnership(application, employer);

        StudentProfile studentProfile = findStudentProfile(application.getStudent().getId());

        Review review = buildReview(dto, application, employer, studentProfile);
        Review saved = reviewRepository.save(review);

        application.setStatus(ApplicationStatus.CLOSED);
        application.setReview(saved);
        applicationRepository.save(application);

        return reviewMapper.toPostResponseDto(saved);
    }

    private Application findApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + applicationId));
    }

    private User findEmployerUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new ForbiddenException("Only employer can leave review");
        }
        return user;
    }

    private Employer findEmployer(User user) {
        return employerRepository.findEmployerByUserId(user.getId())
                .orElseThrow(() -> new ForbiddenException("Employer profile not found for user"));
    }

    private void validateOwnership(Application application, Employer employer) {
        if (!application.getVacancy().getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("You can review only applications for your vacancies");
        }
    }

    private StudentProfile findStudentProfile(Long studentId) {
        return studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException(
                        "StudentProfile not found with id: " + studentId));
    }

    private Review buildReview(ReviewPostRequestDto dto, Application application,
                               Employer employer, StudentProfile student) {
        Review review = new Review();
        review.setApplication(application);
        review.setEmployer(employer);
        review.setStudent(student);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }
}