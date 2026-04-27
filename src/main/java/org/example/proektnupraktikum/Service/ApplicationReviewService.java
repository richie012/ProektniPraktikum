package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.ReviewDto;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.ReviewRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ApplicationReviewService {
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewDto createOrUpdateReview(Long applicationId, ReviewDto dto, String userEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!(user.getEmployer() != null && application.getVacancy().getEmployer().getId().equals(user.getEmployer().getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can leave review for this application");
        }
        Review review = application.getReview();
        if (review == null) {
            review = new Review();
            review.setEmployer(user.getEmployer());
            review.setStudent(application.getStudent());
        }
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review = reviewRepository.save(review);
        application.setReview(review);
        applicationRepository.save(application);
        ReviewDto result = new ReviewDto();
        result.setId(review.getId());
        result.setRating(review.getRating());
        result.setComment(review.getComment());
        result.setEmployerId(review.getEmployer() != null ? review.getEmployer().getId() : null);
        result.setStudentId(review.getStudent() != null ? review.getStudent().getId() : null);
        return result;
    }

    public ReviewDto getReview(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        Review review = application.getReview();
        if (review == null) return null;
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setEmployerId(review.getEmployer() != null ? review.getEmployer().getId() : null);
        dto.setStudentId(review.getStudent() != null ? review.getStudent().getId() : null);
        return dto;
    }
}
