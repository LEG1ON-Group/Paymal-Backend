package com.example.paymal.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReq {
    private String phone;
    private String password;
    private String firstName;
    private String lastName;
}
