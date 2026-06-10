package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.SkillScoreResponse;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.SkillScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {
    public final SkillScoringService skillScoringService;
    private final AuthService authService;
    @PostMapping("/analyze")
    public ResponseEntity<SkillScoreResponse> analyze(){
        User currentUser=authService.getCurrentUser();
        SkillScore score=skillScoringService.analyzeScore(currentUser);
        return ResponseEntity.ok(toResponse(score));
    }

    @GetMapping("/score")
    public ResponseEntity<SkillScoreResponse> getScore(){
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
