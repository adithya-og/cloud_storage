package com.dev.cloudstorage.dto;

import lombok.Data;
import com.dev.cloudstorage.model.User;

@Data
public class SignupResponse {
    private Boolean success;
    private String message;
    private User user;
    
    public SignupResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public SignupResponse(Boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }
}