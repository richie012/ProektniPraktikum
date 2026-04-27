package org.example.proektnupraktikum.Dto.Vacancy.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VacancyResponse {

    private Long id;
    private String title;
    private String description;
    private String companyName;

}
