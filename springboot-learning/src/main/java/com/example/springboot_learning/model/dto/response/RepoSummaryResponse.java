package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data @Builder
public class RepoSummaryResponse {
    private int totalRepos;
    private int totalStars;
    private int totalForks;
    private String mostUsedLanguage;
    private Map<String ,Long> languageBreakDown;
    private int reposWithDescription;
    private double documentationRate;



}
