package com.belvinard.rbac_mfa_starter_springboot.service;

import com.belvinard.rbac_mfa_starter_springboot.exception.InvalidRefreshTokenException;
import com.belvinard.rbac_mfa_starter_springboot.model.RefreshToken;
import com.belvinard.rbac_mfa_starter_springboot.model.User;
import com.belvinard.rbac_mfa_starter_springboot.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Issues and rotates opaque refresh tokens. The raw token is handed to the
 * caller exactly once (at creation) and never persisted or logged — only its
 * SHA-256 hash is stored, so it can be looked up without ever being readable
 * from the database.
 *
 * <p>Rotation: every successful refresh revokes the presented token and issues
 * a new one. If a token that's already revoked is presented again, that's a
 * strong signal it was stolen and reused — every token for that user is
 * revoked immediately so a legitimate user simply has to log in again.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 32; // 256 bits
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public String create(User user) {
        String rawToken = generateRawToken();

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepository.save(entity);

        return rawToken;
    }

    /**
     * Validates and rotates a refresh token, returning the user it belonged to
     * and a brand-new raw refresh token to hand back to the client.
     */
    @Transactional
    public RotationResult rotate(String rawToken) {
        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (existing.isRevoked()) {
            log.warn("Reused refresh token detected for user {} — revoking all sessions", existing.getUser().getUsername());
            refreshTokenRepository.revokeAllByUser(existing.getUser());
            throw new InvalidRefreshTokenException("Refresh token reuse detected — all sessions revoked, please log in again");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        String newRawToken = create(existing.getUser());
        return new RotationResult(existing.getUser(), newRawToken);
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public record RotationResult(User user, String rawToken) {
    }
}
