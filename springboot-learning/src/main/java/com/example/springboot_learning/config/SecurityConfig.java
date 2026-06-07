package com.example.springboot_learning.config;

import com.example.springboot_learning.filter.JwtAuthFilter;
import com.example.springboot_learning.service.impl.OAuth2UserServiceImpl;
import com.example.springboot_learning.service.impl.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailServiceImpl userDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2UserServiceImpl oAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth->oauth
                        .userInfoEndpoint(userInfo->userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler));
        return http.build();
    }
     @Bean
     public AuthenticationProvider authenticationProvider(){
         DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
         daoAuthenticationProvider.setUserDetailsService(userDetailsService);
         daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
         return daoAuthenticationProvider;
     }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws  Exception{
        return config.getAuthenticationManager();

    }
}
