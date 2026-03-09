package com.example.paymal.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateReq {
    private String firstname;
    private String lastname;
    private UUID courseId;
    private String phone;
}
