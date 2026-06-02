package com.example.springboot_learning.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message="Inavlaid email")
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message="Pasword is required")
    private String password;

}
