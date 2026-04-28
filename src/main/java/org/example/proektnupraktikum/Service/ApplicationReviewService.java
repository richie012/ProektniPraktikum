package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.ReviewDto;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Entity.Review;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Repository.ApplicationRepository;
import org.example.proektnupraktikum.Repository.ReviewRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Service.Mapper.ReviewMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationReviewService {
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewDto createOrUpdateReview(
            Long applicationId,
            ReviewDto dto,
            String userEmail
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        boolean isOwner = user.getEmployer() != null
                && user.getEmployer().getId().equals(application.getVacancy().getEmployer().getId());
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can leave review for this application");
        }

        Review review = resolveReview(application, user);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review = reviewRepository.save(review);

        application.setReview(review);
        applicationRepository.save(application);

        return reviewMapper.toDto(review);
    }

    public Optional<ReviewDto> getReview(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        return Optional.ofNullable(application.getReview())
                .map(reviewMapper::toDto);
    }

    private Review resolveReview(Application application, User user) {
        Review review = application.getReview();
        if (review != null) return review;

        Review newReview = new Review();
        newReview.setEmployer(user.getEmployer());
        newReview.setStudent(application.getStudent());
        return newReview;
    }
}
