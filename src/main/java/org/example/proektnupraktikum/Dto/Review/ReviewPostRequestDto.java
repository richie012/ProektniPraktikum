package org.example.proektnupraktikum.Dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ReviewPostRequestDto {
    private Long applicationId;
    private String comment;
    private int rating;
}
