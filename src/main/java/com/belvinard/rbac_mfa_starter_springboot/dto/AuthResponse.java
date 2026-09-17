package com.belvinard.rbac_mfa_starter_springboot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String username;
    private String role;
    private long expiresInMs;
}
