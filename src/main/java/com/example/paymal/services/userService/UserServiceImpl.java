package com.example.paymal.services.userService;

import com.example.paymal.model.entity.Role;
import com.example.paymal.model.entity.User;
import com.example.paymal.model.request.UserReq;
import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.repositories.RoleRepository;
import com.example.paymal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Override
    public HttpEntity<?> addUser(UserReq userData) {

        Role role = addUserRoleIfAbsent();

        User newUser = new User(
                userData.getPhone(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getPassword(),
                role
        );
        User savedUser = repository.save(newUser);
        return ResponseEntity.ok(ApiResponse.success(savedUser));
    }


    public Role addUserRoleIfAbsent() {
        Role userRole = roleRepository.findByRoleName("ROLE_USER");
        if (userRole == null) {
            return roleRepository.save(Role.builder()
                    .roleName("ROLE_USER")
                    .build());
        }

        return roleRepository.findByRoleName("ROLE_USER");
    }

    @Override
    public HttpEntity<?> getMe(User user) {
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @Override
    public HttpEntity<?> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(repository.findAll()));
    }

    @Override
    public HttpEntity<?> editUser(UUID id, UserReq dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        repository.save(user);

        return ResponseEntity.ok(ApiResponse.success(null, "User updated"));
    }

    @Override
    public HttpEntity<?> deleteUser(UUID id) {
        repository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted"));
    }
}
