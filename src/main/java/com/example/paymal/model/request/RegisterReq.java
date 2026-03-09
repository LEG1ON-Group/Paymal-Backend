package com.example.paymal.model.request;

import lombok.Data;

@Data
public class RegisterReq {
    private String phone;
    private String firstName;
    private String lastName;
    private String password;
}
