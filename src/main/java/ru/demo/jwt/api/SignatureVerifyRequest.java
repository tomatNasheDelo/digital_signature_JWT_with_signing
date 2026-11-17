package ru.demo.jwt.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignatureVerifyRequest(
        @NotBlank(message = "payload is required")
        @Size(max = 20000, message = "payload is too large")
        String payload,
        @NotBlank(message = "signature is required")
        @Size(max = 5000, message = "signature is too large")
        String signatureBase64
) {
}
