package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.request.LoginRequest;
import com.example.springboot_learning.model.dto.request.RegisterRequest;
import com.example.springboot_learning.model.dto.request.UpdateProfileRequest;
import com.example.springboot_learning.model.dto.response.AuthResponse;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.util.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login and profile management")

public class AuthController {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    @Operation(summary = "Register a new user",
            description = "Creates a new user account with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }


    @Operation(summary = "Login", description = "Authenticate with email and password, returns JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
   public ResponseEntity<Map<String,String>> login(@RequestBody @Valid LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        UserDetails userDetails=userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String token=jwtService.generateToken(userDetails);;
        return ResponseEntity.ok(Map.of("token",token));
    }
    @GetMapping("/test-env")
    public String testEnv(@Value("${GITHUB_CLIENT_ID}") String clientId) {
        return "Client ID starts with: " + clientId.substring(0, 4);
    }


    @Operation(summary = "Update profile",
            description = "Update name, password or GitHub username")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/update")
    private ResponseEntity<AuthResponse>updateProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest){
        return ResponseEntity.ok(authService.updateProfile(updateProfileRequest));
    }
}