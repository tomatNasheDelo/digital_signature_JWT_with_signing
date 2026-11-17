package ru.demo.jwt.api;

public record JwtValidateResponse(boolean valid, String subject, String issuer, String role) {
}
