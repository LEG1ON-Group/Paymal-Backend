package com.example.paymal.handlers;

import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.Meta;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MyAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Meta meta = Meta.builder()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Unauthorized access!")
                .success(false)
                .build();

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .meta(meta)
                .build();

        response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
    }
}
