package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder@Data
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;


}
