package org.example.proektnupraktikum.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private Long id;
    private Integer rating;
    private String comment;
    private Long employerId;
    private Long studentId;
}

