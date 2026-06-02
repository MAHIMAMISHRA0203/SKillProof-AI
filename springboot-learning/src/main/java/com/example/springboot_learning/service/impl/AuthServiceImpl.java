package com.example.springboot_learning.service.impl;
import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.exception.CustomException.EmailAlreadyExistException;
import com.example.springboot_learning.model.dto.request.LoginRequest;
import com.example.springboot_learning.model.dto.request.RegisterRequest;
import com.example.springboot_learning.model.dto.response.AuthResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {
       if (userRepository.existsByEmail(request.getEmail()) ){
           throw new EmailAlreadyExistException("Email Already Exist"+request.getEmail());
       }
       User user= User.builder()
               .name(request.getName())
               .email(request.getEmail())
               .password(request.getPassword())
               .role(request.getRole())
               .isVerified(false)
               .build();
       userRepository.save(user);


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
}
