package com.example.paymal.services.authService;

import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.Meta;

import com.example.paymal.model.entity.Role;
import com.example.paymal.model.entity.User;
import com.example.paymal.model.request.LoginReq;
import com.example.paymal.model.request.RegisterReq;
import com.example.paymal.repositories.RoleRepository;
import com.example.paymal.repositories.UserRepository;
import com.example.paymal.services.JWTService.JWTServiceImpl;
import com.example.paymal.services.recaptchaService.RecaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepo;
    private final JWTServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;

    @Override
    public HttpEntity<?> login(LoginReq dto, String token) {
        recaptchaService.verify(token);
        String phone = validatePhoneNumber(dto);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phone, dto.getPassword()));
        return ResponseEntity.ok(generateResponse(phone));
    }

    private static String validatePhoneNumber(LoginReq dto) {
        return dto.getPhone().startsWith("+") ? dto.getPhone() : "+" + dto.getPhone();
    }

    private ApiResponse<User> generateResponse(String phone) {
        User user = userRepository.findByPhone(phone);
        String access_token = jwtService.generateJWTToken(user);

        Meta meta = Meta.builder()
                .token(access_token)
                .statusCode(200)
                .message("success")
                .success(true)
                .build();

        return ApiResponse.success(user, meta);
    }

    @Override
    public HttpEntity<?> refreshToken(String refreshToken) {
        String id = jwtService.extractUserFromJwt(refreshToken);
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow();
        String access_token = jwtService.generateJWTToken(user);

        Meta meta = Meta.builder()
                .token(access_token)
                .statusCode(200)
                .message("success")
                .success(true)
                .build();

        return ResponseEntity.ok(ApiResponse.success(user, meta));
    }

    @Override
    public HttpEntity<?> register(RegisterReq dto, String token) {
        recaptchaService.verify(token);
        if (dto.getPhone() == null || dto.getPassword() == null) {
            throw new com.example.paymal.exceptions.CustomException("Phone and Password must not be null", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (dto.getFirstName() == null || dto.getFirstName().trim().length() < 4) {
            throw new com.example.paymal.exceptions.CustomException("Ism kamida 4 ta harfdan iborat bo'lishi kerak", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        String phoneValidationResult = com.example.paymal.utils.PhoneValidator.validateUzbekPhone(dto.getPhone());
        if (!phoneValidationResult.startsWith("Raqam valid")) {
            throw new com.example.paymal.exceptions.CustomException(phoneValidationResult, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        String password = dto.getPassword();
        if (password.length() < 8 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new com.example.paymal.exceptions.CustomException("Parol kamida 8 ta belgidan iborat bo'lishi va kamida 1 ta harf va 1 ta raqam qatnashishi kerak", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new com.example.paymal.exceptions.CustomException("Ushbu telefon raqam allaqachon ro'yxatdan o'tgan", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        User newUser = new User();
        newUser.setPhone(dto.getPhone());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role userRole = roleRepo.findByRoleName("ROLE_USER");
        if (userRole == null) {
            throw new com.example.paymal.exceptions.CustomException("Role 'USER' not found in the database", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
        newUser.setRole(userRole);

        userRepository.save(newUser);
        LoginReq loginDto = new LoginReq();
        loginDto.setPhone(dto.getPhone());
        loginDto.setPassword(dto.getPassword());
        String phone = validatePhoneNumber(loginDto);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phone, dto.getPassword()));
        return ResponseEntity.ok(generateResponse(phone));
    }
}