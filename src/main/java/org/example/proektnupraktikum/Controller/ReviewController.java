package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Review.ReviewPostRequestDto;
import org.example.proektnupraktikum.Dto.Review.ReviewPostResponseDto;
import org.example.proektnupraktikum.Service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewPostResponseDto createReview(@RequestBody ReviewPostRequestDto dto, Authentication authentication) {
        return reviewService.createReview(dto, authentication.getName());
    }
}
