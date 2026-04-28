package org.example.proektnupraktikum.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Dto.Auth.Request.AuthRequest;
import org.example.proektnupraktikum.Dto.Auth.Response.AuthResponse;
import org.example.proektnupraktikum.Entity.Employer;
import org.example.proektnupraktikum.Entity.Enum.Role;
import org.example.proektnupraktikum.Entity.StudentProfile;
import org.example.proektnupraktikum.Entity.User;
import org.example.proektnupraktikum.Repository.EmployerRepository;
import org.example.proektnupraktikum.Repository.StudentProfileRepository;
import org.example.proektnupraktikum.Repository.UserRepository;
import org.example.proektnupraktikum.Service.Security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


/**
 * Контроллер аутентификации
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация, вход и получение информации о пользователе")
public class AuthController {

    private final StudentProfileRepository studentProfileRepository;
    private final EmployerRepository employerRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Зарегистрировать пользователя, стандартная роль - студент<br>
     * Если указано в запросе, может быть зарегистрирован работодатель
     *
     * @param request данные регистрируемого пользователя
     * @return токен авторизации
     */
    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя (студент или работодатель)")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(null, "Email already registered"));
        }

        Role role = request.getRole() != null ? request.getRole() : Role.STUDENT;

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        if (role == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            studentProfileRepository.save(profile);
        } else if (role == Role.EMPLOYER) {
            Employer employer = new Employer();
            employer.setUser(user);
            employer.setCompanyName("Company");
            employerRepository.save(employer);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, "User created"));
    }

    /**
     * Войти в систему, получить токен аутентификации
     *
     * @param request данные пользователя
     * @return токен аутентификации
     */
    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и получение JWT токена")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(token, "Logged in");
    }

    /**
     * Получить данные о зарегистрированном пользователе
     *
     * @param authentication данные аутентификации текущего пользователя
     * @return возвращает почту, роль, идентификатор студента/работодателя
     */
    @Operation(summary = "Информация о текущем пользователе", description = "Получить email, роль и id профиля текущего пользователя")
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        if (user.getRole() == Role.STUDENT) {
            StudentProfile profile = studentProfileRepository
                    .findByUser(user)
                    .orElse(null);

            if (profile != null) {
                response.put("studentId", profile.getId());
            }
        } else if (user.getRole() == Role.EMPLOYER) {
            Employer employer = employerRepository.findByUser(user).orElseGet(() -> {
                Employer created = new Employer();
                created.setUser(user);
                created.setCompanyName("Company");
                return employerRepository.save(created);
            });

            response.put("employerId", employer.getId());
        }

        return response;
    }
}
