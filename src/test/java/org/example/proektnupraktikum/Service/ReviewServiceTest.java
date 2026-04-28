package org.example.proektnupraktikum.Service;

import org.example.proektnupraktikum.Dto.Review.ReviewPostRequestDto;
import org.example.proektnupraktikum.Dto.Review.ReviewPostResponseDto;
import org.example.proektnupraktikum.Entity.*;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Exception.ForbiddenException;
import org.example.proektnupraktikum.Exception.NotFoundException;
import org.example.proektnupraktikum.Exception.UnauthorizedException;
import org.example.proektnupraktikum.Service.Mapper.ReviewMapper;
import org.example.proektnupraktikum.Repository.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewPostRequestDto dto;
    private User studentUser;
    private User employerUser;
    private Employer employer;
    private StudentProfile studentProfile;
    private Application application;

    @BeforeEach
    void setUp() {
        dto = new ReviewPostRequestDto(10L, "Great student!", 5);

        studentUser = buildUser(1L, "student@test.com", Role.STUDENT);
        employerUser = buildUser(2L, "employer@test.com", Role.EMPLOYER);

        employer = new Employer();
        employer.setId(2L);

        studentProfile = new StudentProfile();
        studentProfile.setId(1L);

        Vacancy vacancy = new Vacancy();
        vacancy.setEmployer(employer);

        application = new Application();
        application.setId(10L);
        application.setStatus(ApplicationStatus.PENDING);
        application.setVacancy(vacancy);
        application.setStudent(studentProfile);
    }


    @Test
    @DisplayName("throws 404 when application not found")
    void throwsWhenApplicationNotFound() {
        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(dto, employerUser.getEmail()))
                .isInstanceOf(NotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws 401 when user not found by email")
    void throwsWhenUserNotFound() {
        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(dto, employerUser.getEmail()))
                .isInstanceOf(UnauthorizedException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws 403 when user is not an employer")
    void throwsWhenUserIsNotEmployer() {
        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(studentUser.getEmail())).thenReturn(Optional.of(studentUser));

        assertThatThrownBy(() -> reviewService.createReview(dto, studentUser.getEmail()))
                .isInstanceOf(ForbiddenException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws 403 when employer profile not found for user")
    void throwsWhenEmployerProfileNotFound() {
        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.of(employerUser));
        when(employerRepository.findEmployerByUserId(employerUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(dto, employerUser.getEmail()))
                .isInstanceOf(ForbiddenException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws 403 when employer does not own the vacancy")
    void throwsWhenEmployerDoesNotOwnVacancy() {
        Employer anotherEmployer = new Employer();
        anotherEmployer.setId(99L);

        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.of(employerUser));
        when(employerRepository.findEmployerByUserId(employerUser.getId())).thenReturn(Optional.of(anotherEmployer));

        assertThatThrownBy(() -> reviewService.createReview(dto, employerUser.getEmail()))
                .isInstanceOf(ForbiddenException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws 404 when student profile not found")
    void throwsWhenStudentProfileNotFound() {
        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.of(employerUser));
        when(employerRepository.findEmployerByUserId(employerUser.getId())).thenReturn(Optional.of(employer));
        when(studentProfileRepository.findById(studentProfile.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(dto, employerUser.getEmail()))
                .isInstanceOf(NotFoundException.class);


        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("successfully creates review and closes the application")
    void successfullyCreatesReview() {
        Review savedReview = buildSavedReview();
        ReviewPostResponseDto expectedResponse = new ReviewPostResponseDto(
                savedReview.getId(), dto.getComment(), dto.getRating(),
                employer.getId(), studentProfile.getId(), application.getId()
        );

        when(applicationRepository.findById(dto.getApplicationId())).thenReturn(Optional.of(application));
        when(userRepository.findByEmail(employerUser.getEmail())).thenReturn(Optional.of(employerUser));
        when(employerRepository.findEmployerByUserId(employerUser.getId())).thenReturn(Optional.of(employer));
        when(studentProfileRepository.findById(studentProfile.getId())).thenReturn(Optional.of(studentProfile));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toPostResponseDto(savedReview)).thenReturn(expectedResponse);

        ReviewPostResponseDto response = reviewService.createReview(dto, employerUser.getEmail());

        assertThat(response.getId()).isEqualTo(savedReview.getId());
        assertThat(response.getComment()).isEqualTo(dto.getComment());
        assertThat(response.getRating()).isEqualTo(dto.getRating());

        verify(reviewRepository).save(any(Review.class));
        verify(applicationRepository).save(application);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.CLOSED);
        assertThat(application.getReview()).isEqualTo(savedReview);
    }

    private User buildUser(Long id, String email, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }

    private Review buildSavedReview() {
        Review review = new Review();
        review.setId(1L);
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setEmployer(employer);
        review.setStudent(studentProfile);
        review.setApplication(application);
        return review;
    }
}