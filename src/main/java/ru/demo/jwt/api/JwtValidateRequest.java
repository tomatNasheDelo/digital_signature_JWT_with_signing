package ru.demo.jwt.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JwtValidateRequest(
        @NotBlank @Size(max = 10000) String token
) {
}
