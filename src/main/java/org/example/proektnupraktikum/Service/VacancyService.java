package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    public List<VacancyResponse> getAllVacancies() {
        return vacancyRepository.findAll().stream().map(v -> {
            VacancyResponse dto = new VacancyResponse();
            dto.setId(v.getId());
            dto.setTitle(v.getTitle());
            dto.setDescription(v.getDescription());
            dto.setCompanyName(v.getEmployer().getCompanyName());
            return dto;
        }).toList();
    }

    public Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));
    }

}
