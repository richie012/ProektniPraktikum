package org.example.proektnupraktikum.Dto.Vacancy.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacancyResponse {

    private Long id;
    private String title;
    private String description;
    private String companyName;

}
