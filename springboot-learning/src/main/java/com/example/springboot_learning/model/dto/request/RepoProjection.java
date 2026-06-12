package com.example.springboot_learning.model.dto.request;

public interface RepoProjection {
    Long getGithubRepoId();
    String getRepoName();
    String getLanguage();
    Integer getStars();
    Integer getForks();
    String getRepoUrl();


}
