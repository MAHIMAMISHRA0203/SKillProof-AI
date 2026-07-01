package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.response.AiInsightResponse;
import com.example.springboot_learning.model.dto.response.RecruiterProfileResponse;
import com.example.springboot_learning.model.dto.response.RecruiterSearchResponse;
import com.example.springboot_learning.model.dto.response.RepoItemResponse;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import com.example.springboot_learning.repository.SkillScoreRepository;
import com.example.springboot_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterService {
    private final UserRepository userRepository;
    private final SkillScoreRepository skillScoreRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final AiInsightService aiInsightService;
    public RecruiterProfileResponse getProfile(String githubUsername){
        User user=userRepository.findByGithubUsername(githubUsername)
                .orElseThrow(()->new CustomException.ResourceNotFoundException("Developer not found with this username"));
        SkillScore score=skillScoreRepository.findByUser_Id(user.getId())
                .orElseThrow(()->new CustomException.ResourceNotFoundException(
                        "No skill score found for: " + githubUsername +
                                ". Developer needs to sync their GitHub first."));
        List<GithubRepository> repos=githubRepositoryRepository.findByUserId(user.getId());
        List<RepoItemResponse>topRepos=githubRepositoryRepository
                .findTop5ByUserId(user.getId(), PageRequest.of(0,5))
                .stream()
                .map(this::toRepoItemResponse)
                .toList();
        Map<String ,Long> languageBreakDown=repos.stream()
                .filter(r->r.getLanguage()!=null)
                .collect(Collectors.groupingBy(GithubRepository::getLanguage,Collectors.counting()));
        String mostUsedLanguage=languageBreakDown.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        AiInsightResponse insights=aiInsightService.generateInsights(user);
        return RecruiterProfileResponse.builder()
                .name(user.getName())
                .githubUsername(user.getGithubUsername())
                .overallScore(score.getOverallScore())
                .consistencyScore(score.getConsistencyScore())
                .diversityScore(score.getDiversityScore())
                .documentationScore(score.getDocumentationScore())
                .codeQualityScore(score.getCodeQualityScore())
                .grade(score.getGrade())
                .status(score.getStatus())
                .skillSummary(insights.getSkillSummary())
                .readmeQualityScore(insights.getReadmeQualityScore())
                .totalRepos(repos.size())
                .totalStars(githubRepositoryRepository.sumStarsByUserId(user.getId()))
                .totalForks(githubRepositoryRepository.sumForksByUserId(user.getId()))
                .mostUsedLanguage(mostUsedLanguage)
                .languageBreakdown(languageBreakDown)
                .topRepos(topRepos)
                .lastAnalyzedAt(score.getUpdatedAt())
                .build();

    }
    public List<RecruiterSearchResponse>searchDevelopers(String language,Integer minScore){
        List<Long>userIds=githubRepositoryRepository.findUserIdsByLanguage(language);
        if(userIds.isEmpty())return List.of();
        return userIds.stream()
                .distinct()
                .map(userId->{
                    User  user=userRepository.findById(userId).orElse(null);
                    if(user==null)return null;
                    SkillScore score=skillScoreRepository
                            .findByUser_Id(userId).orElse(null);
                    if(score==null)return null;
                    if(minScore!=null && score.getOverallScore()!=null && score.getOverallScore()<minScore)return null;
                    List<GithubRepository>repos=githubRepositoryRepository
                            .findByUserId(userId);
                    Map<String,Long>langCount=repos.stream()
                            .filter(r->r.getLanguage()!=null)
                            .collect(Collectors.groupingBy(GithubRepository::getLanguage,Collectors.counting()));
                    String mostUsed=langCount.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse("N/A");
                    return RecruiterSearchResponse.builder()
                            .name(user.getName())
                            .githubUsername(user.getGithubUsername())
                            .overallScore(score.getOverallScore())
                            .grade(score.getGrade())
                            .mostUsedLanguage(mostUsed)
                            .totalRepos(repos.size())
                            .build();

                })
                .filter(r->r!=null)
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
