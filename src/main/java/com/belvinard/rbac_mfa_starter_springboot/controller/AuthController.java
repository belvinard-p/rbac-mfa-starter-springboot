package com.belvinard.rbac_mfa_starter_springboot.controller;

import com.belvinard.rbac_mfa_starter_springboot.dto.AuthResponse;
import com.belvinard.rbac_mfa_starter_springboot.dto.LoginRequest;
import com.belvinard.rbac_mfa_starter_springboot.dto.RegisterRequest;
import com.belvinard.rbac_mfa_starter_springboot.dto.TokenRequest;
import com.belvinard.rbac_mfa_starter_springboot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody TokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(@Valid @RequestBody TokenRequest request) {
        authService.logout(request.getRefreshToken());
    }
}
