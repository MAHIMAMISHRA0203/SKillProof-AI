package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiInsightResponse {
    private int readmeQualityScore;
    private String skillSummary;
    private int totalReposAnalyzed;
}