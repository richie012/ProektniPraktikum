package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationStatusUpdateRequest;
import org.example.proektnupraktikum.Dto.Application.Request.ReviewRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public Application apply(@RequestBody ApplicationRequest request) {
        return applicationService.apply(request);
    }

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

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateRequest request,
            Authentication authentication
    ) {
        return applicationService.updateStatus(id, request.getStatus(), authentication.getName());
    }

    @GetMapping("/{id}")
    public ApplicationResponse getApplicationById(@PathVariable Long id, Authentication authentication) {
        return applicationService.getApplicationById(id, authentication.getName());
    }

    @PostMapping("/{id}/review")
    public ApplicationResponse leaveReview(@PathVariable Long id, @RequestBody ReviewRequest request, Authentication authentication) {
        return applicationService.leaveReview(id, request, authentication.getName());
    }

}
