package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.RecruiterProfileResponse;
import com.example.springboot_learning.model.dto.response.RecruiterSearchResponse;
import com.example.springboot_learning.service.impl.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recruiter")
@RequiredArgsConstructor
@Tag(name = "Recruiter", description = "Public endpoints for recruiters to view developer profiles")

public class RecruiterController {
private final RecruiterService recruiterService;
@GetMapping("/{githubUsername}/profile")
@Operation(
        summary = "Get full developer profile",
        description = "Returns complete profile including skill scores, AI insights, and top repos")
    public ResponseEntity<RecruiterProfileResponse>getProfile(@Parameter(description = "GitHub username of the developer", example = "MAHIMAMISHRA0203")
                                                              @PathVariable String githubUsername){
    return ResponseEntity.ok(recruiterService.getProfile(githubUsername));
}
    @GetMapping("/search")
    @Operation(
            summary = "Search developers",
            description = "Search developers by primary language and minimum skill score"
    )
    @PreAuthorize("hasRole('RECRUITER')")

    public ResponseEntity<List<RecruiterSearchResponse>> searchDevelopers(
            @Parameter(description = "Programming language", example = "Java")
            @RequestParam String language,
            @Parameter(description = "Minimum overall score (0-100)", example = "50")
            @RequestParam(required = false) Integer minScore) {
        return ResponseEntity.ok(
                recruiterService.searchDevelopers(language, minScore));
    }
}
