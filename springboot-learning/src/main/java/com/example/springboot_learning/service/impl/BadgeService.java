package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.dto.response.BadgeResponse;
import com.example.springboot_learning.model.dto.response.SkillScoreResponse;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import com.example.springboot_learning.repository.SkillScoreRepository;
import jakarta.validation.constraints.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final SkillScoreRepository skillScoreRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;
    public List<BadgeResponse> computeBadges(User user) {
        SkillScore score = skillScoreRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new CustomException.ResourceNotFoundException("No SkillScore found. Please sync github first"));
        List<GithubRepository> repos = githubRepositoryRepository.findByUserId(user.getId());
        List<BadgeResponse> badges = new ArrayList<>();
        Map<String, Long> languageCount = repos.stream()
                .filter(r -> r.getLanguage() != null)
                .collect(Collectors.groupingBy(GithubRepository::getLanguage, Collectors.counting()));
        languageCount.entrySet().stream()
                .filter(e -> e.getValue() >= 5)
                .forEach(e -> badges.add(BadgeResponse.builder()
                        .name(e.getKey() + "Expert")
                        .description("Has 5+ repositories in " + e.getKey())
                        .emoji("woohoo")
                        .category("language")
                        .build()));
        int totalStars = githubRepositoryRepository.sumStarsByUserId(user.getId());
        if (totalStars > 10) {
            badges.add(BadgeResponse.builder()
                    .name("Star Developer")
                    .description("Earned more than 10 stars across repositories")
                    .emoji("⭐")
                    .category("activity")
                    .build());
        }


        if (score.getDocumentationScore() != null
                && score.getDocumentationScore() > 70) {
            badges.add(BadgeResponse.builder()
                    .name("Documentation Pro")
                    .description("Maintains excellent documentation across repos")
                    .emoji("📚")
                    .category("quality")
                    .build());
        }
        if (score.getConsistencyScore() != null
                && score.getConsistencyScore() > 80) {
            badges.add(BadgeResponse.builder()
                    .name("Consistent Coder")
                    .description("Shows strong coding consistency over past 6 months")
                    .emoji("🔥")
                    .category("activity")
                    .build());
        }
        if (score.getDiversityScore() != null
                && score.getDiversityScore() > 60) {
            badges.add(BadgeResponse.builder()
                    .name("Polyglot Developer")
                    .description("Works across 6+ programming languages")
                    .emoji("🌈")
                    .category("language")
                    .build());
        }
        if (score.getCodeQualityScore() != null
                && score.getCodeQualityScore() > 70) {
            badges.add(BadgeResponse.builder()
                    .name("AI Verified")
                    .description("Portfolio quality verified by AI analysis")
                    .emoji("🤖")
                    .category("ai")
                    .build());
        }
        if (score.getOverallScore() != null
                && score.getOverallScore() >= 80) {
            badges.add(BadgeResponse.builder()
                    .name("High Achiever")
                    .description("Overall skill score of 80 or above")
                    .emoji("🎯")
                    .category("quality")
                    .build());
        }

        if (badges.isEmpty()) {
            badges.add(BadgeResponse.builder()
                    .name("Getting Started")
                    .description("Keep building! Badges unlock as you grow your portfolio.")
                    .emoji("🌱")
                    .category("activity")
                    .build());
        }

        return badges;
    }
}
