package org.example.proektnupraktikum.Service.Mapper;

import org.example.proektnupraktikum.Dto.ReviewDto;
import org.example.proektnupraktikum.Entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "employer.id", target = "employerId")
    @Mapping(source = "student.id", target = "studentId")
    ReviewDto toDto(Review review);
}
