package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.AiInsightResponse;
import com.example.springboot_learning.model.dto.response.AnalyzeFullResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.AiInsightService;
import com.example.springboot_learning.service.impl.GithubApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "AI-powered developer skill analysis")
@RequestMapping("/api/v1/ai")
public class AiInsightController {
    private final AiInsightService aiInsightService;
    private final AuthService authService;
    private final GithubApiService githubApiService;
    @GetMapping("/insight")
    @Operation(
            summary = "Generate AI insights",
            description = "Uses Groq Llama3 to analyze GitHub repos and generate README quality score + skill summary"
    )
    private ResponseEntity<AiInsightResponse> getInsights(){
        User currentUser=authService.getCurrentUser();
        return  ResponseEntity.ok(aiInsightService.generateInsights(currentUser));

    }
    @PostMapping("/analyze-full")
    @Operation(
            summary = "Full AI analysis",
            description = "Syncs GitHub repos, computes rule-based scores, runs AI code quality scoring, generates skill summary — all in one call"
    )
    public ResponseEntity<AnalyzeFullResponse> fullAnalysis(){
        User currentUser=authService.getCurrentUser();
        githubApiService.syncAndAnalyze(currentUser);
        return ResponseEntity.ok(aiInsightService.fullAnalysis(currentUser));
    }


}
