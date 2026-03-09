package com.example.paymal.services.JWTService;

import com.example.paymal.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {
    @Override
    public String generateJWTToken(User user) {
        UUID id = user.getId();
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", user.getPhone());
        Date hourFromCurrentTime = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
//        Date hourFromCurrentTime = new Date(System.currentTimeMillis() + (1000 * 10));
        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(hourFromCurrentTime)
                .setIssuedAt(new Date())
                .setSubject(id.toString())
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateJWTRefreshToken(User users) {
        UUID id = users.getId();
        return Jwts.builder().
                setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30))
//                setExpiration(new Date(System.currentTimeMillis() + (1000 * 25)))
                .setIssuedAt(new Date())
                .setSubject(id.toString())
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateTelegramToken(User user) {
        UUID id = user.getId();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)
                ))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(id.toString())
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("TEVHMU9OX1NFQ1JFVF9LRVlfRk9SX1BBWU1FTlRfU1lTVEVN");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUserFromJwt(String token) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    }
}
