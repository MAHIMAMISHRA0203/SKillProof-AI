package com.example.springboot_learning.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String REPO_SYNC_TOPIC="repo.synced";
    public static final String AI_SYNC_TOPIC="ai.analysis.requested";
    @Bean
    public NewTopic repoSyncTopic(){
        return TopicBuilder.name(REPO_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic aiAnalysisTopic(){
        return TopicBuilder.name(AI_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }


}
