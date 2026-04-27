package org.example.proektnupraktikum.Dto.Application.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequest {

    private Long EmployerId;
    private Long studentId;
    private Long vacancyId;
    private String coverLetter;

}