package com.example.springboot_learning.model.dto.response;

public interface RepoProjection {
    Long getGithubRepoId();
    String getRepoName();
    String getLanguage();
    Integer getStars();
    Integer getForks();
    String getRepoUrl();


}
