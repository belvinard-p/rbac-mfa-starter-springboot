package com.belvinard.rbac_mfa_starter_springboot.controller;

import com.belvinard.rbac_mfa_starter_springboot.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Minimal authenticated-identity check — also doubles as the Phase 3 role-demo starting point. */
@RestController
public class MeController {

    @GetMapping("/api/me")
    public Map<String, String> me(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(
                "username", principal.getUsername(),
                "role", principal.getUser().getRole().name()
        );
    }
}
