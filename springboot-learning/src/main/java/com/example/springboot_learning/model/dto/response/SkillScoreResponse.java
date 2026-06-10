package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;
import com.example.springboot_learning.model.entity.SkillScore.ScoreStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class SkillScoreResponse {
    private Long id;
    private Integer overAllScore;
    private Integer ConsistencyScore;
    private Integer consistencyScore;
    private Integer diversityScore;
    private Integer documentationScore;
    private String grade;
    private ScoreStatus status;
    private LocalDateTime updatedAt;
}
