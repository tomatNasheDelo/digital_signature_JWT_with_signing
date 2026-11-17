package ru.demo.jwt.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignatureRequest(
        @NotBlank(message = "payload is required")
        @Size(max = 20000, message = "payload is too large")
        String payload
) {
}
