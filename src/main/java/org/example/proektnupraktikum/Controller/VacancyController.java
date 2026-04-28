package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Request.VacancyCreateRequest;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Service.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер вакансий
 */
@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    /**
     * Получить все вакансии по строке
     *
     * @param search поисковая строка
     * @return список вакансий
     */
    @GetMapping
    public List<VacancyResponse> getAll(@RequestParam(required = false) String search) {
        return vacancyService.getAll(search);
    }

    /**
     * Получить вакансию по идентификатору
     *
     * @param id идентификатор вакансии
     * @return вакансия
     */
    @GetMapping("/{id}")
    public Vacancy getById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }

    /**
     * Создать новую вакансию
     *
     * @param request        данные о вакансии
     * @param authentication данные об аутентифицированном пользователе
     * @return созданная вакансия
     */
    @PostMapping
    public ResponseEntity<VacancyResponse> create(
            @RequestBody VacancyCreateRequest request,
            Authentication authentication
    ) {
        VacancyResponse created = vacancyService.createVacancy(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
