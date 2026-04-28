package org.example.proektnupraktikum.Service;

import org.example.proektnupraktikum.Dto.Vacancy.Request.VacancyCreateRequest;
import org.example.proektnupraktikum.Dto.Vacancy.Response.VacancyResponse;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Entity.Vacancy;
import org.example.proektnupraktikum.Exception.BadRequestException;
import org.example.proektnupraktikum.Exception.ForbiddenException;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Service.Mapper.VacancyMapper;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VacancyService")
class VacancyServiceTest {

    @Mock private VacancyRepository vacancyRepository;
    @Mock private EmployerRepository employerRepository;
    @Mock private UserRepository userRepository;
    @Mock private VacancyMapper vacancyMapper;

    @InjectMocks private VacancyService vacancyService;

    private User studentUser;
    private User employerUser;
    private VacancyCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        studentUser = buildUser(1L, "student@test.com", Role.STUDENT);
        employerUser = buildUser(2L, "employer@test.com", Role.EMPLOYER);

        validRequest = new VacancyCreateRequest();
        validRequest.setTitle("Java Developer");
        validRequest.setDescription("Backend position");
    }

    @Test
    @DisplayName("throws BadRequestException when title is null")
    void throwsWhenTitleIsNull() {
        VacancyCreateRequest request = new VacancyCreateRequest();
        request.setDescription("desc");

        assertThatThrownBy(() -> vacancyService.createVacancy(request, employerUser.getEmail()))
                .isInstanceOf(BadRequestException.class);

        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws BadRequestException when description is null")
    void throwsWhenDescriptionIsNull() {
        VacancyCreateRequest request = new VacancyCreateRequest();
        request.setTitle("title");

        assertThatThrownBy(() -> vacancyService.createVacancy(request, employerUser.getEmail()))
                .isInstanceOf(BadRequestException.class);

        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws ForbiddenException when user is not an employer")
    void throwsWhenUserIsNotEmployer() {
        when(userRepository.findByEmail(studentUser.getEmail())).thenReturn(Optional.of(studentUser));

        assertThatThrownBy(() -> vacancyService.createVacancy(validRequest, studentUser.getEmail()))
                .isInstanceOf(ForbiddenException.class);

        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("successfully creates vacancy and returns response")
    void successfullyCreatesVacancy() {
        VacancyResponse expectedResponse = new VacancyResponse(1L, validRequest.getTitle(), validRequest.getDescription(), "Company");

        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.of(employerUser));
        when(employerRepository.findByUser(any())).thenReturn(Optional.empty());
        when(employerRepository.save(any())).thenAnswer(inv -> {
            Employer e = inv.getArgument(0);
            e.setId(2L);
            return e;
        });
        when(vacancyRepository.save(any())).thenAnswer(inv -> {
            Vacancy v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });
        when(vacancyMapper.toResponse(any(Vacancy.class))).thenReturn(expectedResponse);

        VacancyResponse response = vacancyService.createVacancy(validRequest, employerUser.getEmail());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(validRequest.getTitle());
        assertThat(response.getDescription()).isEqualTo(validRequest.getDescription());

        verify(vacancyRepository).save(any(Vacancy.class));
    }

    @Test
    @DisplayName("throws NotFoundException when vacancy does not exist")
    void throwsWhenVacancyNotFound() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.getVacancyById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Vacancy not found");
    }


    private User buildUser(Long id, String email, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}