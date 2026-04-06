package com.dev.cloudstorage.dto;

import lombok.Data;
import com.dev.cloudstorage.model.User;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private User user;
    
    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}