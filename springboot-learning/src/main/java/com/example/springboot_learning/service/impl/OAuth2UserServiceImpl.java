package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.model.entity.Role;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
@Component

@RequiredArgsConstructor
public class OAuth2UserServiceImpl  extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
     @Override
    public OAuth2User  loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
//         does the heavy lifting — it calls https://api.github.com/user with the token GitHub gave us and returns all profile attributes as a map.
         OAuth2User oauth=super.loadUser(userRequest);
         String email=oauth.getAttribute("email");
         String name=oauth.getAttribute("name");
         String login=oauth.getAttribute("login");
         if(email==null ||email.isBlank()){
            email= login+"@github.com";
         }
         if(name==null ||name.isEmpty()){
             name=login;
         }
         String finalemail=email;
         String finalname=name;
         String finallogin=login;
         User user =userRepository.findByEmail(email)
                 .orElseGet(()->userRepository.save(
                         User.builder()
                                 .email(finalemail)
                                 .name(finalname)
                                 .password("")
                                 .role(Role.USER)
                                 .githubUsername(finallogin)
                                 .isVerified(true)
                                 .build()

                 ));
         if(user.getUsername()==null || !user.getGithubUsername().equals(login)){
             user.setGithubUsername(login);
             userRepository.save(user);


         }


         return oauth;
     }
}
