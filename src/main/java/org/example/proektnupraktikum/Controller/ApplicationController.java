package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Application.Request.ApplicationRequest;
import org.example.proektnupraktikum.Dto.Application.Response.ApplicationResponse;
import org.example.proektnupraktikum.Entity.Application;
import org.example.proektnupraktikum.Service.ApplicationService;
import org.springframework.http.HttpStatus;
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

}
