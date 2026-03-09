package com.example.paymal.services.authService;

import com.example.paymal.model.request.LoginReq;
import com.example.paymal.model.request.RegisterReq;
import org.springframework.http.HttpEntity;

public interface AuthService {
    HttpEntity<?> login(LoginReq dto, String token);

    HttpEntity<?> refreshToken(String refreshToken);

    HttpEntity<?> register(RegisterReq dto, String token);
}