package com.belvinard.rbac_mfa_starter_springboot.dto;

import com.belvinard.rbac_mfa_starter_springboot.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // Optional — defaults to VIEWER when omitted (see AuthService).
    // A real product would never let a client pick ADMIN at sign-up; fine for this demo.
    private Role role;
}
