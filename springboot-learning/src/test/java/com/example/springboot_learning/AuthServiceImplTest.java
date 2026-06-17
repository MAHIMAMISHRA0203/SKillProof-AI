package com.example.springboot_learning;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.request.RegisterRequest;
import com.example.springboot_learning.model.dto.request.UpdateProfileRequest;
import com.example.springboot_learning.model.dto.response.AuthResponse;
import com.example.springboot_learning.model.entity.Role;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import com.example.springboot_learning.service.impl.AuthServiceImpl;
import com.example.springboot_learning.service.impl.SkillScoringService;
import com.example.springboot_learning.util.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;


    @Mock
    private SkillScoringService skillScoringService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setup(){
        testUser= User.builder()
                .id(1L)
                .name("Mahima")
                .email("mahima@gmaail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .isVerified(false)
                .build();

        registerRequest=new RegisterRequest();
        registerRequest.setName("Mahima");
        registerRequest.setEmail("mahima@gmail.com");
        registerRequest.setPassword("mahima123");
        registerRequest.setRole(Role.USER);
    }
    @Test
    @DisplayName("register:should save user when email is new")
    void register_shouldSaveUser_whenEmailIsNew(){
         when(userRepository.existsByEmail("mahima@gmail.com")).thenReturn(false);
         when(passwordEncoder.encode("mahima123")).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);
        authService.register(registerRequest);
        verify(userRepository,times(1)).save(Mockito.any(User.class));
        verify(skillScoringService,times(1)).initializeScore(Mockito.any(User.class));

    }
    @Test
    @DisplayName("update:should update name when name is provided")
    void  updateProfile_shouldUpdateName_whenNameProvided(){
        UpdateProfileRequest request=new UpdateProfileRequest();
        request.setName("Mahima Updated");
        org.springframework.security.core.Authentication auth=mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn("mahima@gmail.com");
        org.springframework.security.core.context.SecurityContextHolder
                .getContext().setAuthentication(auth);
        when(userRepository.findByEmail("mahima@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        // ACT
        AuthResponse response = authService.updateProfile(request);

        // ASSERT
        verify(userRepository, times(1)).save(Mockito.any(User.class));
        assertThat(testUser.getName()).isEqualTo("Mahima Updated");

    }
    @Test
    @DisplayName("updateprofile:should not update name when name is not null")
    void updateProfile_shouldNotUpdateName_whenNameIsNull(){
        UpdateProfileRequest request =new UpdateProfileRequest();
        request.setName(null);
        org.springframework.security.core.Authentication auth=mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn("mahima@gmail.com");
        org.springframework.security.core.context.SecurityContextHolder
                .getContext().setAuthentication(auth);

        when(userRepository.findByEmail("mahima@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        authService.updateProfile(request);

        assertThat(testUser.getName()).isEqualTo("Mahima");


    }
    @Test
    @DisplayName("getCurrentUser: should throw exception when user not found")
    void getCurrentUser_shouldThrowException_whenUserNotFound() {
        org.springframework.security.core.Authentication auth =
                mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn("notfound@gmail.com");
        org.springframework.security.core.context.SecurityContextHolder
                .getContext().setAuthentication(auth);

        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(CustomException.ResourceNotFoundException.class, () -> authService.getCurrentUser());
    }
}
