package com.example.paymal.services.JWTService;

import com.example.paymal.model.entity.User;

import java.security.Key;

public interface JWTService {
    String generateJWTToken(User users);

    String generateJWTRefreshToken(User users);
    String generateTelegramToken(User users);

    Key getSigningKey();

    String extractUserFromJwt(String token);
}
