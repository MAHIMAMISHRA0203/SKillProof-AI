package com.example.springboot_learning.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ValueGenerationType;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Table(name="users")
@Builder
@NoArgsConstructor
@Data

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
     private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String githubUsername;
    private String githubToken;

    @Column(nullable = false)
    private Boolean isVerified=false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;



}
