package ru.demo.jwt.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CryptoApiIntegrationTest {
    private static final KeyPair TEST_KEY_PAIR = createKeyPair();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void rsaKeys(DynamicPropertyRegistry registry) {
        registry.add("app.crypto.public-key-pem", () -> Base64.getEncoder().encodeToString(TEST_KEY_PAIR.getPublic().getEncoded()));
        registry.add("app.crypto.private-key-pem", () -> Base64.getEncoder().encodeToString(TEST_KEY_PAIR.getPrivate().getEncoded()));
    }

    @Test
    void signsAndVerifiesThroughApi() throws Exception {
        String payload = "integration payload";
        String signatureJson = mockMvc.perform(post("/api/signature/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.createObjectNode().put("payload", payload).toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode signature = objectMapper.readTree(signatureJson).get("signatureBase64");

        mockMvc.perform(post("/api/signature/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.createObjectNode()
                                .put("payload", payload)
                                .put("signatureBase64", signature.asText()).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void createsAndValidatesJwtThroughApi() throws Exception {
        String tokenJson = mockMvc.perform(post("/api/jwt/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"issuer":"demo-issuer","subject":"alice","role":"reader","ttlSeconds":300}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(tokenJson).get("token").asText();

        mockMvc.perform(post("/api/jwt/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.createObjectNode().put("token", token).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.subject").value("alice"));
    }

    private static KeyPair createKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
