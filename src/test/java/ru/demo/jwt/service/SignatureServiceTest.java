package ru.demo.jwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureServiceTest {
    private SignatureService signatureService;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        signatureService = new SignatureService(generator.generateKeyPair());
    }

    @Test
    void signsAndVerifiesPayloadAndDetectsModification() {
        String payload = "this is signed data";
        String signature = signatureService.sign(payload);

        assertNotNull(signature);
        assertTrue(signatureService.verify(payload, signature));
        assertFalse(signatureService.verify(payload + " modified", signature));
    }
}
