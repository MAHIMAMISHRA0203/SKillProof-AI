package com.example.springboot_learning.service.impl;
import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.exception.CustomException.EmailAlreadyExistException;
import com.example.springboot_learning.model.dto.request.LoginRequest;
import com.example.springboot_learning.model.dto.request.RegisterRequest;
import com.example.springboot_learning.model.dto.request.UpdateProfileRequest;
import com.example.springboot_learning.model.dto.response.AuthResponse;
import com.example.springboot_learning.model.entity.AuditLog;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SkillScoringService skillScoringService;

    @Override
    public void register(RegisterRequest request) {
       if (userRepository.existsByEmail(request.getEmail()) ){
           throw new EmailAlreadyExistException("Email Already Exist"+request.getEmail());
       }
       User user= User.builder()
               .name(request.getName())
               .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword()))
               .role(request.getRole())
               .isVerified(false)
               .build();
       userRepository.save(user);
       skillScoringService.initializeScore(user);

        auditLogService.log(
                user.getId(),
                user.getEmail(),
                "USER_REGISTERED",
                "New user registered with role: " + user.getRole(),
                AuditLog.AuditStatus.SUCCESS
        );
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user=userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new CustomException.ResourceNotFoundException("User not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new CustomException.InvalidCredentialsException("Email or Password  is Invalid  ");
        }
        String token=jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build() ;
    }

    @Override
    public User getCurrentUser() {
        String email= SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException.ResourceNotFoundException("User does not exist"));

    }

    @Override
    public AuthResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        if (request.getGithubUsername() != null && !request.getGithubUsername().isBlank()) {
            user.setGithubUsername(request.getGithubUsername());
        }

        userRepository.save(user);
        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public User getUserByGithubUsername(String githubUsername) {
        return userRepository.findByGithubUsername(githubUsername)
                .orElseThrow(() -> new CustomException.ResourceNotFoundException(
                        "Developer not found with GitHub username: " + githubUsername));
    }
}
