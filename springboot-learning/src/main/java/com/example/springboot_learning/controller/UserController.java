package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.AuthResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final AuthService authService;
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(){
        return ResponseEntity.ok("You are authenticated. Welcome ");
    }
     @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(){
        User user= authService.getCurrentUser();
        return ResponseEntity.ok(
                AuthResponse.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build());
     }

}
