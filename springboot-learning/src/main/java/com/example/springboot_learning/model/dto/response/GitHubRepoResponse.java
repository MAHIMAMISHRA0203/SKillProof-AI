package com.example.springboot_learning.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GitHubRepoResponse {
    private Long id;
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    private String description;
    private String language;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("stargazers_count")
    private Integer stargazersCount;

    @JsonProperty("forks_count")
    private Integer forksCount;

    @JsonProperty("open_issues_count")
    private Integer openIssuesCount;

    @JsonProperty("private")
    private Boolean isPrivate;

    @JsonProperty("pushed_at")
    private LocalDateTime pushedAt;







}
