package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.model.dto.response.GitHubRepoResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.springboot_learning.model.entity.GithubRepository;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service

public class GithubApiService {
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final RestClient restClient=RestClient.builder()
            .baseUrl("https://api.github.com")
            .build();
    public List<GithubRepository> fetchAndSaveRepos(User user){
        GitHubRepoResponse[]repos=restClient.get()
                .uri("/users/{username}/repos?per_page=100&sort=pushed", user.getGithubUsername())
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GitHubRepoResponse[].class);
        if(repos==null ) return List.of();
        return Arrays.stream(repos)
                .map(ghRepo->{
                    GithubRepository existing=  githubRepositoryRepository
                            .findByGithubRepoId(ghRepo.getId())
                            .orElse(new GithubRepository());
                    existing.setUser(user);
                    existing.setGithubRepoId(ghRepo.getId());
                    existing.setRepoName(ghRepo.getName());
                    existing.setFullName(ghRepo.getFullName());
                    existing.setDescription(ghRepo.getDescription());
                    existing.setLanguage(ghRepo.getLanguage());
                    existing.setRepoUrl(ghRepo.getHtmlUrl());
                    existing.setStars(ghRepo.getStargazersCount());
                    existing.setForks(ghRepo.getForksCount());
                    existing.setOpenIssues(ghRepo.getOpenIssuesCount());
                    existing.setIsPrivate(ghRepo.getIsPrivate());
                    existing.setPushedAt(ghRepo.getPushedAt());
                    return githubRepositoryRepository.save(existing);
                })
                .toList();

    }
}
