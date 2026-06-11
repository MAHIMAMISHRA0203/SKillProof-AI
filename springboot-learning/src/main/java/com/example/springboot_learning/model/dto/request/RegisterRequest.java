package com.example.springboot_learning.model.dto.request;

import com.example.springboot_learning.model.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;


    @Email(message="Invalid email format")
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message ="password cannot be null")
    @Size(min=6,message="password should be atleast 6 charaters")
    private String password;
    private String githubUsername;

    private Role role = Role.USER;


}
