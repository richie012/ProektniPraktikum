package org.example.proektnupraktikum.Dto.Auth.Request;

import lombok.Getter;
import lombok.Setter;
import org.example.proektnupraktikum.Entity.Enum.Role;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
    private Role role;
}