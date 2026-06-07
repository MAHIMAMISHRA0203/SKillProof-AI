package com.example.springboot_learning.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name="repositories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity



public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Column(nullable = false,unique = true)
    private Long githubRepoId;

    @Column(nullable=false)
    private String repoName;

    private String fullName;
    private String description;
    private String language;
    private String repoUrl;

    private Integer stars;
    private Integer forks;
    private Integer openIssues;

    private Boolean isPrivate;
    private LocalDateTime pushedAt;


    @UpdateTimestamp
    private LocalDateTime lastSyncAt;


}
