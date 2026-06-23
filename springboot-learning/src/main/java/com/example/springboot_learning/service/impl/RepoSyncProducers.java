package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.config.KafkaTopicConfig;
import com.example.springboot_learning.model.dto.request.RepoSyncedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoSyncProducers {
    private final KafkaTemplate<String , RepoSyncedEvent> kafkaTemplate;
    public void  publicRepoSynced(Long userId, String userEmail,
                                  String githubUsername, int repoCount){
        RepoSyncedEvent event=RepoSyncedEvent.builder()
                .userId(userId)
                .userEmail(userEmail)
                .githubUsername(githubUsername)
                .repoCount(repoCount)
                .triggeredAt(LocalDateTime.now().toString())
                .build();
        kafkaTemplate.send(KafkaTopicConfig.REPO_SYNCED_TOPIC
        ,String.valueOf(userId),event);
        log.info("Published repo.synced event for user: {} with {} repos",
                userEmail, repoCount);

    }

}
