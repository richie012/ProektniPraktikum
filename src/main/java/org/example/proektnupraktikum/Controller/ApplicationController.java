package org.example.proektnupraktikum.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationStatusUpdateRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/***
 * Контроллер заявок
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Заявки", description = "Операции с заявками студентов и работодателей")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Принять заявку
     *
     * @param request заявка
     * @return принятая заявка
     */
    @Operation(summary = "Подать заявку", description = "Студент подает заявку на вакансию")
    @PostMapping
    public ApplicationResponse apply(@RequestBody ApplicationRequest request) {
        return applicationService.apply(request);
    }

    /**
     * Найти заявку по идентификатору работника или студента
     *
     * @param studentId  ид студнета
     * @param employerId ид работника
     * @return список заявок по полю
     */
    @Operation(summary = "Найти заявки по id студента или работодателя", description = "Получить список заявок по id студента или работодателя")
    @GetMapping
    public List<ApplicationResponse> findApplicationsById(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long employerId
    ) {
        if (studentId == null && employerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId or employerId is required");
        }
        if (studentId != null && employerId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use only one filter: studentId or employerId");
        }
        return applicationService.findApplicationsById(studentId, employerId);
    }

    /**
     * Обновить статус заявки
     *
     * @param id             ид заявки
     * @param request        дто с новым статусом
     * @param authentication авторизованный пользователь
     * @return результат обновления
     */
    @Operation(summary = "Обновить статус заявки", description = "Работодатель обновляет статус заявки")
    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateRequest request,
            Authentication authentication
    ) {
        return applicationService.updateStatus(id, request.getStatus(), authentication.getName());
    }

    /***
     * Получить заявку по идентификатору заявки
     * @param id идентификатор заявки
     * @param authentication авторизованный пользователь
     *
     * @return заявка
     */
    @Operation(summary = "Получить заявку по id", description = "Получить заявку по идентификатору")
    @GetMapping("/{id}")
    public ApplicationResponse getApplicationById(@PathVariable Long id, Authentication authentication) {
        return applicationService.getApplicationById(id, authentication.getName());
    }
}
