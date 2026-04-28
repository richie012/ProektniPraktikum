package org.example.proektnupraktikum.Service.Mapper;

import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ReviewMapper.class)
public interface ApplicationMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "student.phone", target = "studentPhone")
    @Mapping(source = "student.skills", target = "studentSkills")
    @Mapping(source = "student.user.email", target = "studentEmail")
    @Mapping(source = "vacancy.id", target = "vacancyId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "review", target = "review")
    ApplicationResponse toResponse(Application application);
}