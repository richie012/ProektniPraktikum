package org.example.proektnupraktikum.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Request.VacancyCreateRequest;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
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
@Tag(name = "Вакансии", description = "Операции с вакансиями")
public class VacancyController {

    private final VacancyService vacancyService;

    /**
     * Получить все вакансии по строке
     *
     * @param search поисковая строка
     * @return список вакансий
     */
    @Operation(summary = "Получить все вакансии", description = "Получить список всех вакансий по поисковой строке")
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
    @Operation(summary = "Получить вакансию по id", description = "Получить вакансию по идентификатору")
    @GetMapping("/{id}")
    public VacancyResponse getById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }

    /**
     * Создать новую вакансию
     *
     * @param request        данные о вакансии
     * @param authentication данные об аутентифицированном пользователе
     * @return созданная вакансия
     */
    @Operation(summary = "Создать вакансию", description = "Создать новую вакансию (только для работодателя)")
    @PostMapping
    public ResponseEntity<VacancyResponse> create(
            @RequestBody VacancyCreateRequest request,
            Authentication authentication
    ) {
        VacancyResponse created = vacancyService.createVacancy(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
