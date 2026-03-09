package com.example.paymal.controllers;

import com.example.paymal.model.entity.User;
import com.example.paymal.model.request.UserReq;
import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.repositories.UserRepository;
import com.example.paymal.services.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService service;
    private final UserRepository userRepository;

    @PostMapping
    public HttpEntity<?> addUser(@RequestBody UserReq dto) {
        return service.addUser(dto);
    }

    @GetMapping
    public HttpEntity<?> getUsers() {
        return service.getUsers();
    }


    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByPhone(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/{id}")
    public HttpEntity<?> editUser(@PathVariable UUID id, @RequestBody UserReq dto) {
        return service.editUser(id, dto);
    }


    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        return service.deleteUser(id);
    }
}
