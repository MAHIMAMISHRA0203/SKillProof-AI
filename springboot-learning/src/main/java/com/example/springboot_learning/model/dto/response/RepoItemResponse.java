package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RepoItemResponse {
    private Long githubRepoId;
    private String repoName;
    private String fullName;
    private String description;
    private String language;
    private String repoUrl;
    private Integer stars;
    private Integer forks;
    private Boolean isPrivate;
    private LocalDateTime pushedAt;
}
