package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.ReviewDto;
import org.example.proektnupraktikum.Service.ApplicationReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications/{applicationId}/review")
@RequiredArgsConstructor
public class ApplicationReviewController {
    private final ApplicationReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createOrUpdateReview(
            @PathVariable Long applicationId,
            @RequestBody ReviewDto reviewDto,
            @RequestHeader("X-User-Email") String userEmail
    ) {
        return ResponseEntity.ok(reviewService.createOrUpdateReview(applicationId, reviewDto, userEmail));
    }

    @GetMapping
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long applicationId) {
        return ResponseEntity.ok(reviewService.getReview(applicationId));
    }
}
