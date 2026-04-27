package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    public List<VacancyResponse> getAll(String search) {
        List<Vacancy> vacancies = (search == null || search.isBlank())
                ? vacancyRepository.findAll()
                : vacancyRepository.searchByText(toPrefix(search.trim()));

        return vacancies.stream()
                .map(v -> new VacancyResponse(
                        v.getId(),
                        v.getTitle(),
                        v.getDescription(),
                        v.getEmployer().getCompanyName()
                ))
                .toList();
    }

    public Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));
    }


    private String toPrefix(String search) {
        return Arrays.stream(search.split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> word + ":*")
                .collect(Collectors.joining(" & "));
    }
}
