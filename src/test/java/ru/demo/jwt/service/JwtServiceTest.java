package ru.demo.jwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        jwtService = new JwtService(generator.generateKeyPair());
    }

    @Test
    void createsAndValidatesJwt() {
        String token = jwtService.createToken("demo-issuer", "alice", "admin", 300L);
        var claims = jwtService.validateToken(token);

        assertEquals("alice", claims.getSubject());
        assertEquals("demo-issuer", claims.getIssuer());
        assertEquals("admin", claims.get("role", String.class));
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.createToken("demo-issuer", "alice", "admin", 300L);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");
        assertThrows(CryptoOperationException.class, () -> jwtService.validateToken(tampered));
    }
}
