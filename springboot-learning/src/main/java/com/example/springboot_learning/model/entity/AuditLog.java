package com.example.springboot_learning.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Data
@Builder
@Table(name = "audit_logs")
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action;

    private String details;

    private String ipAddress;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum AuditStatus {
        SUCCESS, FAILURE
    }

}
