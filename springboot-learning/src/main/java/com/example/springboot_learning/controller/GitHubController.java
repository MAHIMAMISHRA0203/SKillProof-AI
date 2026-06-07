package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.GithubApiService;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.Repository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GitHubController {
    private final GithubApiService githubApiService;
    private final AuthService authService;
    @GetMapping("/repos")
    public ResponseEntity<List<GithubRepository>> getRepos(){
        User currentuser=authService.getCurrentUser();
        List<GithubRepository> repos=githubApiService.fetchAndSaveRepos(currentuser);
        return ResponseEntity.ok(repos);
    }

}
