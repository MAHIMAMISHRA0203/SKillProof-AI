package com.example.springboot_learning.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepoSyncedEvent {
    private Long userId;
    private String userEmail;
    private String githubUsername;
    private int repoCount;
    private String triggeredAt;
}
