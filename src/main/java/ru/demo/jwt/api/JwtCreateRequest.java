package ru.demo.jwt.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record JwtCreateRequest(
        @NotBlank @Size(max = 250) String issuer,
        @NotBlank @Size(max = 250) String subject,
        @NotBlank @Size(max = 100) String role,
        @Positive @Max(86400) long ttlSeconds
) {
}
