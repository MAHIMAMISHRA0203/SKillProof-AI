package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.response.AiInsightResponse;
import com.example.springboot_learning.model.dto.request.GroqRequest;
import com.example.springboot_learning.model.dto.response.AnalyzeFullResponse;
import com.example.springboot_learning.model.dto.response.GroqResponse;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import com.example.springboot_learning.repository.SkillScoreRepository;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AiInsightService {
    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.model}")
    private String groqModel;

    private final GithubRepositoryRepository githubRepositoryRepository;
    private final SkillScoreRepository skillScoringRepository;

    private final RestClient.Builder restClientBuilder;

    public AiInsightResponse generateInsights(User user){
        List<GithubRepository> repos=githubRepositoryRepository.findByUserId(user.getId());
        SkillScore score=skillScoringRepository.findByUser_Id(user.getId()).orElse(null);
        String repoContext=buildRepoContext(repos);
        int readmeScore=scoreReadmeQuality(repos);
        String skillSummary=generateSkillSummary(user,repos,score);
        return AiInsightResponse.builder()
                .readmeQualityScore(readmeScore)
                .skillSummary(skillSummary)
                .totalReposAnalyzed(repos.size())
                .build();


    }
    private int scoreReadmeQuality(List<GithubRepository>repos){
        long withDescription=repos.stream()
                .filter(r->r.getDescription()!=null&& !r.getDescription().isBlank())
                .count();
        String  prompt=String.format("""
                 You are a developer profile analyzer.
                                A developer has %d GitHub repositories.
                                %d of them have descriptions.
                                The descriptions are:
                                %s
                
                                Based on the quality and completeness of these descriptions,
                                give a README quality score from 0 to 100.
                                Respond with ONLY a number between 0 and 100. Nothing else.
                                """,
                                repos.size(),
                                withDescription,
                                repos.stream()
                                        .filter(r -> r.getDescription() != null)
                                        .map(r -> "- " + r.getRepoName() + ": " + r.getDescription())
                                        .collect(Collectors.joining("\\n"))
                );
        String response =callGroq(prompt);
        try{
            String cleaned=response.trim().replaceAll("[^0-9]", "");
            int score = Integer.parseInt(cleaned.substring(0, Math.min(cleaned.length(), 3)));
            return Math.min(Math.max(score,0),100);

        }catch(Exception e){
            return 50;
        }
    }
    private String  generateSkillSummary(User user,List<GithubRepository>repos,SkillScore score){
        Map<String ,Long> languageCount=repos.stream()
                .filter(r->r.getLanguage()!=null)
                .collect(Collectors.groupingBy(GithubRepository::getLanguage,Collectors.counting()));

        String topLanguages=languageCount.entrySet().stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(3)
                .map(e->e.getKey()+" (" + e.getValue() + " repos)")
                .collect(Collectors.joining(","));

        String prompt = String.format("""
                You are a technical recruiter writing a developer profile summary.
                Write a professional 2-3 sentence summary for this developer:
                
                Name: %s
                Total Repositories: %d
                Top Languages: %s
                Consistency Score: %d/100 (based on recent GitHub activity)
                Diversity Score: %d/100 (based on number of languages used)
                Documentation Score: %d/100 (based on repo descriptions)
                Overall Skill Score: %d/100
                Grade: %s
                Write a concise, professional summary suitable for a recruiter dashboard.
                                Focus on strengths. Do not mention the scores directly.
                                """,
                                user.getName(),
                                repos.size(),
                                topLanguages,
                                score != null ? score.getConsistencyScore() : 0,
                                score != null ? score.getDiversityScore() : 0,
                                score != null ? score.getDocumentationScore() : 0,
                                score != null ? score.getOverallScore() : 0,
                                score != null ? score.getGrade() : "N/A"
                        );

                        return callGroq(prompt);
    }
    private String callGroq(String userPrompt){
        GroqRequest request = GroqRequest.builder()
                .model(groqModel)
                .maxTokens(500)
                .temperature(0.3)
                .messages(List.of(
                        new GroqRequest.Message("user", userPrompt)
                ))
                .build();

        RestClient client = restClientBuilder.build();

        GroqResponse response = client.post()
                .uri(groqApiUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(GroqResponse.class);

        if (response == null || response.getContent().isBlank()) {
            return "Unable to generate insight at this time.";
        }

        return response.getContent();
    }
    private String buildRepoContext(List<GithubRepository> repos) {
        return repos.stream()
                .limit(10) // only send top 10 to avoid token limits
                .map(r -> r.getRepoName() + " (" + r.getLanguage() + ")")
                .collect(Collectors.joining(", "));
    }
    public int getSiCOdeQuality(User user){
        List<GithubRepository>repos=githubRepositoryRepository.findByUserId(user.getId());
        if(repos.isEmpty()) return 0;
        String portfolioSummary=repos.stream()
                .limit(15)
                .map(r -> String.format("- %s (language: %s, stars: %d, description: %s)",
                        r.getRepoName(),
                        r.getLanguage() != null ? r.getLanguage() : "unknown",
                        r.getStars() != null ? r.getStars() : 0,
                        r.getDescription() != null ? r.getDescription() : "none"))
                .collect(Collectors.joining("\n"));
        String prompt = String.format("""
            You are a senior software engineer evaluating a developer's GitHub portfolio.
            
            Here are their repositories:
            %s
            
            Evaluate the overall code quality signal based on:
            1. Meaningfulness of repository names (generic names like "test", "project1" score lower)
            2. Variety and relevance of languages used
            3. Presence and quality of descriptions
            4. Star count as a signal of useful/shareable work
            5. Overall portfolio depth and professionalism
            
            Give a code quality score from 0 to 100.
            Respond with ONLY a number between 0 and 100. Nothing else.
            """,
                portfolioSummary
        );
        String response=callGroq(prompt);
        try{
            String cleaned=response.trim().replaceAll("[^0-9]","");
            int score=Integer.parseInt(cleaned.substring(0,Math.min(cleaned.length(),3)));
            return  Math.min(Math.max(score,0),100);

        }catch (Exception e){
            return 50;
        }
    }
    public AnalyzeFullResponse fullAnalysis(User user){
        AiInsightResponse insights=generateInsights(user);
        SkillScore score=skillScoringRepository.findByUser_Id(user.getId())
                .orElseThrow(()->new CustomException.ResourceNotFoundException("No score found. Please call /github/sync first."));
        return AnalyzeFullResponse.builder()
                .overAllScore(score.getOverallScore())
                .consistencyScore(score.getConsistencyScore())
                .diversityScore(score.getDiversityScore())
                .documentationScore(score.getDocumentationScore())
                .codeQualityScore(score.getCodeQualityScore())
                .grade(score.getGrade())
                .status(score.getStatus())
                .skillSummary(insights.getSkillSummary())
                .readmeQualityScore(insights.getReadmeQualityScore())
                .totalReposAnalyzed(insights.getTotalReposAnalyzed())
                .analyzedAt(score.getUpdatedAt())
                .build();

    }
}
