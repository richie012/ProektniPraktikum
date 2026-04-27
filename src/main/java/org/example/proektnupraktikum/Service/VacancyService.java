package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Request.VacancyCreateRequest;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;

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

    public VacancyResponse createVacancy(VacancyCreateRequest request, String userEmail) {
        if (request == null
                || request.getTitle() == null || request.getTitle().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and description are required");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getRole() != Role.EMPLOYER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can create vacancies");
        }

        Employer employer = employerRepository.findByUser(user).orElseGet(() -> {
            Employer created = new Employer();
            created.setUser(user);
            created.setCompanyName("Company");
            return employerRepository.save(created);
        });

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle().trim());
        vacancy.setDescription(request.getDescription().trim());
        vacancy.setEmployer(employer);
        vacancy.setCreatedAt(LocalDateTime.now());

        Vacancy saved = vacancyRepository.save(vacancy);
        return new VacancyResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getEmployer().getCompanyName()
        );
    }


    private String toPrefix(String search) {
        return Arrays.stream(search.split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> word + ":*")
                .collect(Collectors.joining(" & "));
    }
}
