package com.example.paymal.services.userService;

import com.example.paymal.model.entity.User;
import com.example.paymal.model.request.UserReq;
import org.springframework.http.HttpEntity;

import java.util.UUID;

public interface UserService {
    HttpEntity<?> addUser(UserReq dto);


    HttpEntity<?> getMe(User user);

    HttpEntity<?> getUsers();


    HttpEntity<?> editUser(UUID id, UserReq dto);

    HttpEntity<?> deleteUser(UUID id);
}
