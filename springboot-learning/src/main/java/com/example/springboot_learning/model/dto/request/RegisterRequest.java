package com.example.springboot_learning.model.dto.request;

import com.example.springboot_learning.model.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body for user registration")

public class RegisterRequest {
    @NotBlank
    @Schema(description = "Full name of the user", example = "Mahima Mishra")

    private String name;


    @Email(message="Invalid email format")
    @NotBlank(message="Email is required")
    @Schema(description = "Email address", example = "mahima@gmail.com")
    private String email;

    @NotBlank(message ="password cannot be null")
    @Size(min=6,message="password should be atleast 6 charaters")
    @Schema(description = "Password (min 6 characters)", example = "mahima123")
    private String password;

    @Schema(description = "GitHub username", example = "MAHIMAMISHRA0203")
    private String githubUsername;

    private Role role = Role.USER;


}
