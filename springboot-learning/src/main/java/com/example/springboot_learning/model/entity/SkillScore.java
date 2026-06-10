package com.example.springboot_learning.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="skill_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private Integer overallScore;
    private Integer codeQualityScore;   // based on SonarQube later
    private Integer consistencyScore;   // commit frequency & streaks
    private Integer diversityScore;     // number of languages used
    private Integer documentationScore;

    private String grade; // "A", "B+", "C" etc — computed from overallScore

    @Enumerated(EnumType.STRING)
    private ScoreStatus status; // tracks where in the pipeline we are

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ScoreStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED}
    }
