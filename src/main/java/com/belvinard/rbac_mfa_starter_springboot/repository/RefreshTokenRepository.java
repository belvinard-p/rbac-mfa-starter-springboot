package com.belvinard.rbac_mfa_starter_springboot.repository;

import com.belvinard.rbac_mfa_starter_springboot.model.RefreshToken;
import com.belvinard.rbac_mfa_starter_springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Runs in its own transaction (REQUIRES_NEW) on purpose: this is called right before
     * the caller throws an exception to signal token reuse, and a RuntimeException would
     * normally roll back the whole transaction — including this revocation. Committing it
     * separately guarantees the security action survives regardless of what happens next.
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user = :user AND r.revoked = false")
    void revokeAllByUser(@Param("user") User user);
}
