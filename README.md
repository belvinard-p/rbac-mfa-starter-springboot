# rbac-mfa-starter-springboot

A Spring Boot starter that adds **role-based access control (RBAC)** and **TOTP two-factor authentication (MFA)** to an API — the two pieces most generic JWT/auth tutorials skip, built from real production experience securing a bank's Internet Banking platform (JWT, Spring Security, BCrypt, per-role permissions).

## Who this is for

Teams that need to add proper role-based permissions and 2FA to an existing or new Spring Boot API quickly, without re-inventing the authentication layer from scratch.

## Status

🚧 Work in progress — built incrementally, phase by phase.

- [x] **Phase 1 — Domain model**: `User`, `Role` (`ADMIN` / `MANAGER` / `VIEWER`), repository, Spring Security `UserDetails` bridge
- [ ] **Phase 2 — JWT authentication**: token generation/validation, auth filter, `/auth/register` and `/auth/login`
- [ ] **Phase 3 — RBAC**: role-protected endpoints, per-role demo routes
- [ ] **Phase 4 — MFA (TOTP)**: enable / verify / disable two-factor authentication
- [ ] **Phase 5 — Hardening**: account lockout after failed attempts, tests, API docs

## Tech stack

Java 21, Spring Boot, Spring Security, Spring Data JPA, H2 (dev), JJWT, TOTP (`dev.samstevens.totp`), Springdoc OpenAPI, Testcontainers.

## Getting started

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. The H2 console is available at `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:rbacdb`
- User: `sa`
- Password: *(empty)*

> The current security configuration is intentionally wide open (`permitAll`) for early development. It will be replaced by the real JWT + RBAC filter chain in Phase 2.

## License

MIT
