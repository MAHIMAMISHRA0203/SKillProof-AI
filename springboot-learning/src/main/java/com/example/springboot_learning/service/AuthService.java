package com.example.springboot_learning.service;

import com.example.springboot_learning.model.dto.request.LoginRequest;
import com.example.springboot_learning.model.dto.request.RegisterRequest;
import com.example.springboot_learning.model.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest loginRequest);
}

