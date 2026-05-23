package com.rtr.AuthUser.dto;
import lombok.Data;

@Data
public  class CreateUserRequest {
        private String email;
        private String password;
        private String customerId;
        
    }