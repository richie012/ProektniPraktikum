package org.example.proektnupraktikum.Dto.Application.Response;

import lombok.Getter;
import lombok.Setter;
import org.example.proektnupraktikum.Dto.ReviewDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String studentSkills;
    private Long vacancyId;
    private String status;
    private LocalDateTime createdAt;

    private ReviewDto review; // отзыв работодателя

}
