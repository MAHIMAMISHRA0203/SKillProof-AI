package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.BadgeResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Developer achievement badges")

public class BadgeController {
private final BadgeService badgeService;
private final AuthService authService;
@GetMapping("/my")
@SecurityRequirement(name = "bearerAuth")
@Operation(summary = "Get my badges",
        description = "Returns all badges earned by the current developer")
    public ResponseEntity<List<BadgeResponse>> getMyBadge(){
    User currentUser=authService.getCurrentUser();
    return ResponseEntity.ok(badgeService.computeBadges(currentUser));
}

    @GetMapping("/developer/{githubUsername}")
    @Operation(summary = "Get developer badges",
            description = "Public endpoint — returns badges for any developer by GitHub username")
    public ResponseEntity<List<BadgeResponse>> getDeveloperBadges(
            @PathVariable String githubUsername) {
        // find user by github username
        User user = authService.getUserByGithubUsername(githubUsername);
        return ResponseEntity.ok(badgeService.computeBadges(user));
    }
}
