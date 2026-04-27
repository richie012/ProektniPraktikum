package org.example.proektnupraktikum.Dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewPostResponseDto {
    private Long id;
    private String comment;
    private Integer rating;
    private Long employerId;
    private Long studentId;
    private Long applicationId;
}
