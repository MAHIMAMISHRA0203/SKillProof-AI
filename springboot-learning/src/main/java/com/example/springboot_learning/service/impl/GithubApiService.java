package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.response.GitHubRepoResponse;
import com.example.springboot_learning.model.dto.response.RepoSummaryResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.springboot_learning.model.entity.GithubRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class GithubApiService {
    private final RestClient restClient;

    private final GithubRepositoryRepository githubRepositoryRepository;

    public List<GithubRepository> fetchAndSaveRepos(User user){
        if(user.getGithubUsername()==null ||user.getGithubUsername().isBlank()){
            throw new CustomException.GeneralException("No  Github Account linked to this user "+ HttpStatus.BAD_REQUEST);
        }
        GitHubRepoResponse[]repos=restClient.get()
                .uri("/users/{username}/repos?per_page=100&sort=pushed", user.getGithubUsername())
                .retrieve()
                .body(GitHubRepoResponse[].class);
        if(repos==null ) return List.of();
        return Arrays.stream(repos)
                .map(ghRepo->mapAndSave(ghRepo,user))
                .toList();


    }

    public GithubRepository getRepoById(Long githubRepoId){
        return githubRepositoryRepository
                .findByGithubRepoId(githubRepoId)
                .orElseThrow(()->new CustomException.GeneralException(
                        "Repository not fond in db "+githubRepoId+HttpStatus.BAD_REQUEST
                ));
    }
    private GithubRepository mapAndSave(GitHubRepoResponse response,User user){
        GithubRepository repo=githubRepositoryRepository
                .findByGithubRepoId(response.getId())
                .orElse(new GithubRepository());
        repo.setUser(user);
        repo.setGithubRepoId(response.getId());
        repo.setRepoName(response.getName());
        repo.setFullName(response.getFullName());
        repo.setDescription(response.getDescription());
        repo.setLanguage(response.getLanguage());
        repo.setRepoUrl(response.getHtmlUrl());
        repo.setStars(response.getStargazersCount());
        repo.setForks(response.getForksCount());
        repo.setOpenIssues(response.getOpenIssuesCount());
        repo.setIsPrivate(response.getIsPrivate());
        repo.setPushedAt(response.getPushedAt());
        return githubRepositoryRepository.save(repo);
    }
    public RepoSummaryResponse getSummaryResponse(User user){
        List<GithubRepository>repos=githubRepositoryRepository.findByUserId(user.getId());
        if(repos.isEmpty()) {
            return RepoSummaryResponse.builder()
                    .totalRepos(0)
                    .totalStars(0)
                    .languageBreakDown(Map.of())
                    .mostUsedLanguage("N/A")
                    .reposWithDescription(0)
                    .documentationRate(0.0)
                    .build();
        }
        Map<String ,Long>languageBreakdown=repos.stream()
                .filter(r->r.getLanguage()!=null)
                .collect(Collectors.groupingBy(
                        GithubRepository::getLanguage,Collectors.counting()
                ));
        String mostUsedLanguage=languageBreakdown.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        int totalStars= githubRepositoryRepository.sumStarsByUserId(user.getId());
        int totalForks= githubRepositoryRepository.sumForksByUserId(user.getId());
        long withDescription=repos.stream()
                .filter(r->r.getDescription()!=null && !r.getDescription().isEmpty())
                .count();
        double docRate=Math.round((withDescription*100.0/repos.size())*10.0)/10.0;
        return RepoSummaryResponse.builder()
                .totalRepos(repos.size())
                .totalStars(totalStars)
                .totalForks(totalForks)
                .languageBreakDown(languageBreakdown)
                .mostUsedLanguage(mostUsedLanguage)
                .reposWithDescription((int) withDescription)
                .documentationRate(docRate)
                .build();
    }

}
