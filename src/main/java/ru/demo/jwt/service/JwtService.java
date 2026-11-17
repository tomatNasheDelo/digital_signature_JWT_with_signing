package ru.demo.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Service
public class JwtService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(KeyPair keyPair) {
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public String createToken(String issuer, String subject, String role, long ttlSeconds) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + ttlSeconds * 1000L);
        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claim("role", role)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new CryptoOperationException("JWT validation failed", exception);
        }
    }
}
