package com.example.springboot_learning.model.dto.response;

import com.example.springboot_learning.model.entity.SkillScore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RecruiterProfileResponse {
    private String name;
    private String githubUsername;
    private Integer overallScore;
    private Integer consistencyScore;
    private Integer diversityScore;
    private Integer documentationScore;
    private Integer codeQualityScore;
    private String grade;
    private SkillScore.ScoreStatus status;


    private String skillSummary;
    private Integer readmeQualityScore;


    private Integer totalRepos;
    private Integer totalStars;
    private Integer totalForks;
    private String mostUsedLanguage;
    private Map<String, Long> languageBreakdown;
    private List<RepoItemResponse> topRepos;

    private LocalDateTime lastAnalyzedAt;

}
