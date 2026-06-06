package com.example.springboot_learning.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)  // add this

@Builder@Data
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;


}
