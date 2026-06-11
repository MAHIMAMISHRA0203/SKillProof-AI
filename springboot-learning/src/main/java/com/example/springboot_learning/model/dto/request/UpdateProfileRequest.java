package com.example.springboot_learning.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String newPassword;
    private String githubUsername;

}
