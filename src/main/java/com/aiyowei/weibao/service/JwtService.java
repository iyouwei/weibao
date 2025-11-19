package com.aiyowei.weibao.service;

import com.aiyowei.weibao.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private Key signingKey;

    @PostConstruct
    public void init() {
        // Defensive checks to give a clear error when configuration is missing.
        String secret = properties.getSecret();
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("Missing configuration: 'jwt.secret' must be set (must be at least 32 bytes). Set it in application.yml or environment variables.");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("Invalid configuration: 'jwt.secret' must be at least 32 bytes long (current length: " + keyBytes.length + "). Provide a longer secret.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId) {
        if (userId == null) {
            return null;
        }
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpiration() * 1000);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Long resolveUserId(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
            return Long.valueOf(claims.getBody().getSubject());
        } catch (Exception ex) {
            return null;
        }
    }

    private String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }
        String value = authorizationHeader.trim();
        if (value.startsWith("Bearer ")) {
            return value.substring(7);
        }
        return value;
    }
}
