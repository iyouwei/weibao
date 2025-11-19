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
        this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
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


