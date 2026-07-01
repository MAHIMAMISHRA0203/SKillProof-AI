package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruiterSearchResponse {
    private String name;
    private String githubUsername;
    private Integer overallScore;
    private String grade;
    private String mostUsedLanguage;
    private Integer totalRepos;
    private String skillSummary;
}