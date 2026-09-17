package com.belvinard.rbac_mfa_starter_springboot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Shared shape for endpoints that only need a refresh token: /auth/refresh and /auth/logout. */
@Getter
@Setter
public class TokenRequest {

    @NotBlank
    private String refreshToken;
}
