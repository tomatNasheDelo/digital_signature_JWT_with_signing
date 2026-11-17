package ru.demo.jwt.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

@Service
public class SignatureService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public SignatureService(KeyPair keyPair) {
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public String sign(String payload) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception exception) {
            throw new CryptoOperationException("Digital signature creation failed", exception);
        }
    }

    public boolean verify(String payload, String signatureBase64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(signatureBase64);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            return signature.verify(decoded);
        } catch (Exception exception) {
            return false;
        }
    }
}
