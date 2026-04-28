package org.example.proektnupraktikum.Service.Mapper;


import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(source = "employer.companyName", target = "companyName")
    VacancyResponse toResponse(Vacancy vacancy);
}
