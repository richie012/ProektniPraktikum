package org.example.proektnupraktikum.Dto.Application.Request;

import lombok.Data;

@Data
public class ReviewRequest {
    private String comment;
    private Integer rating;
}

