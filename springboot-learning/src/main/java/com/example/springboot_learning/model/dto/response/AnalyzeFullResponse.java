package com.example.springboot_learning.model.dto.response;

import com.example.springboot_learning.model.entity.SkillScore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AnalyzeFullResponse {
    private Integer overAllScore;
    private Integer consistencyScore;
    private Integer diversityScore;
    private Integer documentationScore;
    private Integer codeQualityScore;
    private String grade;
    private SkillScore.ScoreStatus status;

    private String skillSummary;
    private Integer readmeQualityScore;
    private Integer totalReposAnalyzed;

    private LocalDateTime analyzedAt;
}
