package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.config.KafkaTopicConfig;
import com.example.springboot_learning.model.dto.request.RepoSyncedEvent;
import com.example.springboot_learning.model.dto.response.AiInsightResponse;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoSyncConsumer {
    private final AiInsightService aiInsightService;
    private final SkillScoringService skillScoringService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    @KafkaListener(
            topics = KafkaTopicConfig.REPO_SYNC_TOPIC,
            groupId = "skillproof-group"
    )
    public void handleRepoSynced(RepoSyncedEvent event){
        log.info("Received repo.synced event for user: {} with {} repos"
                ,event.getUserEmail(),event.getRepoCount());

        try{
            User user=userRepository.findById(event.getUserId())
                    .orElseThrow(()->new RuntimeException("User not found"+event.getUserId()));
            log.info("Starting async AI analysis for user: {}", event.getUserEmail());
            aiInsightService.generateInsights(user);
            AiInsightResponse insights = aiInsightService.generateInsights(user);
            emailService.sendAiInsightsReadyEmail(
                    user.getEmail(),
                    user.getName(),
                    insights.getSkillSummary()
            );
            log.info("Async AI analysis completed for user: {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to process repo.synced event for user {}: {}",
                    event.getUserEmail(), e.getMessage());
        }        }
    }




