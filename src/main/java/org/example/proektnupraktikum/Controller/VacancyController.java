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

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    public List<VacancyResponse> getAll(@RequestParam(required = false) String search) {
        return vacancyService.getAll(search);
    }

    @GetMapping("/{id}")
    public Vacancy getById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }

    @PostMapping
    public ResponseEntity<VacancyResponse> create(
            @RequestBody VacancyCreateRequest request,
            Authentication authentication
    ) {
        VacancyResponse created = vacancyService.createVacancy(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
