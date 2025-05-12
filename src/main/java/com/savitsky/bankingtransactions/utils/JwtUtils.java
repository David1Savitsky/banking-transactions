package com.savitsky.bankingtransactions.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String USER_ID_CLAIM_NAME = "USER_ID";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMillis}")
    private long expirationMillis;

    public String generateToken(Long userId) {
        var now = new Date();
        var expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .claim(USER_ID_CLAIM_NAME, userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(USER_ID_CLAIM_NAME, Long.class);
    }
}
