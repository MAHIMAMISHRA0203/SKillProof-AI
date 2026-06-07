package com.example.springboot_learning.config;

import com.example.springboot_learning.model.entity.Role;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import com.example.springboot_learning.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    public final UserRepository userRepository;
    public final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String login = oAuth2User.getAttribute("login");
        String name  = oAuth2User.getAttribute("name");

        if (email == null || email.isBlank()) {
            email = login + "@github.com";
        }
        if (name == null || name.isBlank()) {
            name = login;
        }

        final String finalEmail = email;
        final String finalName  = name;
        final String finalLogin = login;

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(finalEmail)
                                .name(finalName)
                                .password("")
                                .role(Role.USER)
                                .githubUsername(finalLogin)
                                .isVerified(true)
                                .build()
                ));

        String token = jwtService.generateToken(user);
        response.sendRedirect("http://localhost:4200/oauth2/callback?token=" + token);
    }
}