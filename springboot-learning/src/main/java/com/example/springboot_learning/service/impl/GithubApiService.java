package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.response.GitHubRepoResponse;
import com.example.springboot_learning.model.dto.response.RepoSummaryResponse;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.springboot_learning.model.entity.GithubRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.springboot_learning.model.dto.response.PageRepoResponse;
import com.example.springboot_learning.model.dto.response.RepoItemResponse;

import javax.naming.ldap.PagedResultsControl;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class GithubApiService {
    private final SkillScoringService skillScoringService;
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
    public SkillScore syncAndAnalyze(User user){
        fetchAndSaveRepos(user);
        return skillScoringService.analyzeScore(user);
    }
public PageRepoResponse getPageRepos(User user,int page ,int size,String sortBy,String direction,String language){
        Sort.Direction sortDirection=direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
    ;
        Pageable pageable=PageRequest.of(page,size,Sort.by(sortDirection,sortBy));
        Page<GithubRepository> repoPage=githubRepositoryRepository
                .findByUserIdWithFilter(user.getId(),language,pageable);
        List<RepoItemResponse> items =repoPage.getContent().stream()
                .map(this::toRepoItemResponse)
                .toList();
    return PageRepoResponse.builder()
            .repos(items)
            .currentPage(repoPage.getNumber())
            .totalPages(repoPage.getTotalPages())
            .totalRepos(repoPage.getTotalElements())
            .hasNext(repoPage.hasNext())
            .hasPrevious(repoPage.hasPrevious())
            .build();

}
public List<RepoItemResponse> getTopRepos(User user){
        Pageable top5=PageRequest.of(0,5);
        return githubRepositoryRepository
                .findTop5ByUserId(user.getId(), top5)
                .stream()
                .map(this::toRepoItemResponse)
                .toList();

}
    private RepoItemResponse toRepoItemResponse(GithubRepository repo) {
        return RepoItemResponse.builder()
                .githubRepoId(repo.getGithubRepoId())
                .repoName(repo.getRepoName())
                .fullName(repo.getFullName())
                .description(repo.getDescription())
                .language(repo.getLanguage())
                .repoUrl(repo.getRepoUrl())
                .stars(repo.getStars())
                .forks(repo.getForks())
                .isPrivate(repo.getIsPrivate())
                .pushedAt(repo.getPushedAt())
                .build();
    }
}
