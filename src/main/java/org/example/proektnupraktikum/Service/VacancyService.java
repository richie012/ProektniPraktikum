package org.example.proektnupraktikum.Service;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Vacancy.Request.VacancyCreateRequest;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Service.Mapper.VacancyMapper;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final VacancyMapper vacancyMapper;

    public List<VacancyResponse> getAll(String search) {
        List<Vacancy> vacancies = (search == null || search.isBlank())
                ? vacancyRepository.findAll()
                : vacancyRepository.searchByText(toTsPrefix(search.trim()));

        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public VacancyResponse getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .map(vacancyMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));
    }

    public VacancyResponse createVacancy(VacancyCreateRequest request, String userEmail) {
        validateRequest(request);

        User user = findEmployerUser(userEmail);
        Employer employer = findOrCreateEmployer(user);

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle().trim());
        vacancy.setDescription(request.getDescription().trim());
        vacancy.setEmployer(employer);
        vacancy.setCreatedAt(LocalDateTime.now());

        return vacancyMapper.toResponse(vacancyRepository.save(vacancy));
    }

    private void validateRequest(VacancyCreateRequest request) {
        if (request == null
                || request.getTitle() == null || request.getTitle().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and description are required");
        }
    }

    private User findEmployerUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employer can create vacancies");
        }
        return user;
    }

    private Employer findOrCreateEmployer(User user) {
        return employerRepository.findByUser(user).orElseGet(() -> {
            Employer employer = new Employer();
            employer.setUser(user);
            employer.setCompanyName("Company");
            return employerRepository.save(employer);
        });
    }

    private String toTsPrefix(String search) {
        return Arrays.stream(search.split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> word + ":*")
                .collect(Collectors.joining(" & "));
    }
}
