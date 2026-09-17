package com.belvinard.rbac_mfa_starter_springboot.service;

import com.belvinard.rbac_mfa_starter_springboot.dto.AuthResponse;
import com.belvinard.rbac_mfa_starter_springboot.dto.LoginRequest;
import com.belvinard.rbac_mfa_starter_springboot.dto.RegisterRequest;
import com.belvinard.rbac_mfa_starter_springboot.model.Role;
import com.belvinard.rbac_mfa_starter_springboot.model.User;
import com.belvinard.rbac_mfa_starter_springboot.repository.UserRepository;
import com.belvinard.rbac_mfa_starter_springboot.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Role DEFAULT_ROLE = Role.VIEWER;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : DEFAULT_ROLE)
                .build();
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String rawRefreshToken) {
        RefreshTokenService.RotationResult rotation = refreshTokenService.rotate(rawRefreshToken);
        User user = rotation.user();

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(rotation.rawToken())
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshTokenService.create(user))
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }
}
