package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.dto.response.RepoProjection;
import com.example.springboot_learning.model.dto.response.*;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.GithubApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
@Tag(name = "GitHub", description = "GitHub repository sync and analysis")
@SecurityRequirement(name = "bearerAuth")

public class GitHubController {
    private final GithubApiService githubApiService;
    private final AuthService authService;
    @GetMapping("/repos")
    public ResponseEntity<List<GithubRepository>> getAllRepos(){
        User currentuser=authService.getCurrentUser();
        List<GithubRepository> repos=githubApiService.fetchAndSaveRepos(currentuser);
        return ResponseEntity.ok(repos);
    }
@GetMapping("/repos/{githubRepoId}")
    public ResponseEntity<GithubRepository> getRepoById(@PathVariable long githubRepoId){
        GithubRepository repo=githubApiService.getRepoById(githubRepoId);
        return ResponseEntity.ok(repo);
}
    @GetMapping("/repos/summary")
    public ResponseEntity<RepoSummaryResponse> getRepoSummary() {
        User currentUser = authService.getCurrentUser();
        RepoSummaryResponse summary = githubApiService.getSummaryResponse(currentUser);
        return ResponseEntity.ok(summary);
    }


    @Operation(summary = "Sync GitHub repos + compute skill score",
            description = "Fetches latest repos from GitHub API, saves to DB, and computes skill score in one call")
    @ApiResponse(responseCode = "200", description = "Sync successful, returns computed SkillScore")
    @PostMapping("/sync")
    public ResponseEntity<SkillScoreResponse> syncAndAnalyze(){
        User currentUser=authService.getCurrentUser();
        SkillScore score=githubApiService.syncAndAnalyze(currentUser);
        return ResponseEntity.ok(toResponse(score));
    }
    @GetMapping("/repos/paged")



    @Operation(summary = "Get paginated repos",
            description = "Returns repos with pagination, optional language filter and sorting")
    public ResponseEntity<PageRepoResponse> getPagedRepos(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "pushedAt") String sortBy,
                                                          @RequestParam(defaultValue = "desc") String direction,
                                                          @RequestParam(required = false) String language){
        User currentUser=authService.getCurrentUser();
        return ResponseEntity.ok(githubApiService.getPageRepos(currentUser,page,size,sortBy,direction,language));
    }


    @Operation(summary = "Get top 5 repos by stars")

    @GetMapping("/repos/top")
    public ResponseEntity<List<RepoItemResponse>> getTopRepos(){
        User currentUser=authService.getCurrentUser();
        return ResponseEntity.ok(githubApiService.getTopRepos(currentUser));

    }
    @GetMapping("/repos/lightweight")
    public ResponseEntity<List<RepoProjection>> getLighweightRepos(){
        User currentUser=authService.getCurrentUser();
        return  ResponseEntity.ok(githubApiService.getLightWeightRepos(currentUser));
    }


    @Operation(summary = "Get language statistics",
            description = "Returns repo count, total stars and forks grouped by language")
    @GetMapping("/repos/stats")
    public ResponseEntity<List<LanguageStatsResponse>> getLanguageStats(){
        User currentUser=authService.getCurrentUser();
        return  ResponseEntity.ok(githubApiService.getLanguageStats(currentUser));

    }

    @Operation(summary = "Search repos by name keyword")

    @GetMapping("/repos/search")
    public ResponseEntity<List<RepoItemResponse>>  searchRepos(@RequestParam String keyword){
        User currentUser=authService.getCurrentUser();
        return  ResponseEntity.ok(githubApiService.searchRepos(currentUser,keyword));

    }
    private SkillScoreResponse toResponse(SkillScore score) {
        return SkillScoreResponse.builder()
                .id(score.getId())
                .overAllScore(score.getOverallScore())
                .consistencyScore(score.getConsistencyScore())
                .diversityScore(score.getDiversityScore())
                .documentationScore(score.getDocumentationScore())
                .grade(score.getGrade())
                .status(score.getStatus())
                .updatedAt(score.getUpdatedAt())
                .build();
    }
}
