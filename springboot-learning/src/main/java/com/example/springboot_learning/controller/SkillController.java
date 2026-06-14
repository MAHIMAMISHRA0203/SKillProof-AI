package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.SkillScoreResponse;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.SkillScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill score analysis and retrieval")
@SecurityRequirement(name = "bearerAuth")
public class SkillController {
    private final SkillScoringService skillScoringService;
    private final AuthService authService;

    @Operation(summary = "Analyze skill score",
            description = "Computes skill score from existing repos in DB. Call /github/sync first.") @PostMapping("/analyze")
    public ResponseEntity<SkillScoreResponse> analyze(){
        User currentUser=authService.getCurrentUser();
        SkillScore score=skillScoringService.analyzeScore(currentUser);
        return ResponseEntity.ok(toResponse(score));
    }


    @Operation(summary = "Get current skill score",
            description = "Returns the latest computed skill score from DB or cache")
    @GetMapping("/score")
    public ResponseEntity<SkillScoreResponse> getScore(){
        User currentUser=authService.getCurrentUser();
        SkillScore score=skillScoringService.getScore(currentUser);
        return ResponseEntity.ok(toResponse(score));
    }

    @Operation(summary = "Get skill score history")

    @GetMapping("/history")
    public ResponseEntity<SkillScoreResponse> getScoreHistory(){
        User currentUser=authService.getCurrentUser();
        SkillScore score=skillScoringService.getScore(currentUser);
        return ResponseEntity.ok(toResponse(score));
    }
    private SkillScoreResponse toResponse(SkillScore score){
        return SkillScoreResponse.builder()
                .id(score.getId())
                .overAllScore(score.getOverallScore())
                .consistencyScore(score.getConsistencyScore())
                .diversityScore(score.getDiversityScore())
                .documentationScore(score.getDocumentationScore())
                .grade(score.getGrade())
                .status(score.getStatus())
                .updatedAt(score.getUpdatedAt())
                .build();
    }

}
