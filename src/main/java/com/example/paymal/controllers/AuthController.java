package com.example.paymal.controllers;

import com.example.paymal.model.request.LoginReq;
import com.example.paymal.model.request.RegisterReq;
import com.example.paymal.services.authService.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginReq loginReq, @RequestHeader("recaptcha-token") String token) {
        return authService.login(loginReq, token);
    }

    @PostMapping("/refresh")
    public HttpEntity<?> refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/register")
    public HttpEntity<?> register(@RequestBody RegisterReq registerReq, @RequestHeader("recaptcha-token") String token) {
        return authService.register(registerReq, token);
    }
}