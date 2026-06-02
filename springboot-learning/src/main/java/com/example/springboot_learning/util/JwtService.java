package com.example.springboot_learning.util;

import com.example.springboot_learning.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    public <T>T extractClaim(String token, Function<Claims,T> claimResolver){
        Claims claim=Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimResolver.apply(claim);
    }
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isValid(String token ,User user){
        return extractEmail(token).equals(user.getEmail()) && !isExpired(token);
    }
    public boolean isExpired(String token){
        return extractClaim(token, Claims::getExpiration)
                .before(new Date());

    }
}