package com.example.backend.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerRegisterRequest {
    private String name;
    private String email;
    private String password;
    private String companyName;
    private String businessNumber;
}
