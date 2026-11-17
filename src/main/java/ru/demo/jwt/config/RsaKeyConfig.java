package ru.demo.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {
    @Bean
    public KeyPair rsaKeyPair(
            @Value("${app.crypto.public-key-pem:}") String publicKeyPem,
            @Value("${app.crypto.private-key-pem:}") String privateKeyPem
    ) {
        if (publicKeyPem.isBlank() || privateKeyPem.isBlank()) {
            throw new IllegalStateException("Set RSA_PUBLIC_KEY_PEM and RSA_PRIVATE_KEY_PEM environment variables");
        }

        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            var publicKey = factory.generatePublic(new X509EncodedKeySpec(decodePem(publicKeyPem)));
            var privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(decodePem(privateKeyPem)));
            if (!(publicKey instanceof RSAPublicKey rsaPublic)
                    || !(privateKey instanceof RSAPrivateKey rsaPrivate)
                    || rsaPublic.getModulus().bitLength() < 2048
                    || !rsaPublic.getModulus().equals(rsaPrivate.getModulus())) {
                throw new IllegalArgumentException("RSA keys must match and be at least 2048 bits");
            }
            return new KeyPair(publicKey, privateKey);
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid RSA keys; provide matching X.509 public and PKCS#8 private PEM keys", exception);
        }
    }

    private byte[] decodePem(String pem) {
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        return Base64.getDecoder().decode(normalized);
    }
}
