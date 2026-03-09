package com.example.paymal.config;

import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.Meta;
import com.example.paymal.repositories.UserRepository;
import com.example.paymal.services.JWTService.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MyFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestPath = request.getRequestURI();

        if (requestPath.startsWith("/api")) {
            if (isOpenUrl(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String header = request.getHeader("Authorization");
            String token = null;

            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }


            if (token != null) {
                try {
                    String subject = jwtService.extractUserFromJwt(token);
                    UserDetails userDetails = userRepo.findById(UUID.fromString(subject)).orElseThrow();
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (ExpiredJwtException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    Meta meta = Meta.builder()
                            .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                            .message("Token expired!")
                            .success(false)
                            .build();

                    ApiResponse<Object> apiResponse = ApiResponse.builder()
                            .meta(meta)
                            .build();
                    response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
                    return;
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    Meta meta = Meta.builder()
                            .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                            .message("Invalid token!")
                            .success(false)
                            .build();

                    ApiResponse<Object> apiResponse = ApiResponse.builder()
                            .meta(meta)
                            .build();
                    response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                Meta meta = Meta.builder()
                        .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                        .message("Authorization header missing!")
                        .success(false)
                        .build();

                ApiResponse<Object> apiResponse = ApiResponse.builder()
                        .meta(meta)
                        .build();
                response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isOpenUrl(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        return requestPath.startsWith("/api/auth/")
                || (requestPath.startsWith("/api/files/") && "GET".equalsIgnoreCase(method))
                || requestPath.startsWith("/api/payments");
    }
}
