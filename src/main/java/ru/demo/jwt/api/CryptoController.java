package ru.demo.jwt.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demo.jwt.service.JwtService;
import ru.demo.jwt.service.SignatureService;

@RestController
@RequestMapping("/api")
public class CryptoController {
    private final SignatureService signatureService;
    private final JwtService jwtService;

    public CryptoController(SignatureService signatureService, JwtService jwtService) {
        this.signatureService = signatureService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signature/sign")
    public SignatureResponse sign(@Valid @RequestBody SignatureRequest request) {
        return new SignatureResponse(signatureService.sign(request.payload()));
    }

    @PostMapping("/signature/verify")
    public SignatureVerificationResponse verify(@Valid @RequestBody SignatureVerifyRequest request) {
        return new SignatureVerificationResponse(signatureService.verify(request.payload(), request.signatureBase64()));
    }

    @PostMapping("/jwt/create")
    public JwtCreateResponse createJwt(@Valid @RequestBody JwtCreateRequest request) {
        return new JwtCreateResponse(jwtService.createToken(
                request.issuer(), request.subject(), request.role(), request.ttlSeconds()));
    }

    @PostMapping("/jwt/validate")
    public JwtValidateResponse validateJwt(@Valid @RequestBody JwtValidateRequest request) {
        var claims = jwtService.validateToken(request.token());
        return new JwtValidateResponse(true, claims.getSubject(), claims.getIssuer(), claims.get("role", String.class));
    }
}
