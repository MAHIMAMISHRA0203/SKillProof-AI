package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LanguageStatsResponse {
    private String language;
    private Long repoCOunt;
    private Long totalStats;
    private Long totalForks;

}
