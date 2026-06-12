package com.example.springboot_learning.repository;

import com.example.springboot_learning.model.dto.request.RepoProjection;
import com.example.springboot_learning.model.dto.response.LanguageStatsResponse;
import com.example.springboot_learning.model.entity.GithubRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubRepositoryRepository extends JpaRepository<GithubRepository,Long> {
    List<GithubRepository> findByUserId(Long id);

    Optional<GithubRepository> findByGithubRepoId(Long id);

    @Query("SELECT COALESCE(SUM(r.stars), 0) FROM GithubRepository r WHERE r.user.id = :userId")
    int sumStarsByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(r.forks), 0) FROM GithubRepository r WHERE r.user.id = :userId")
    int sumForksByUserId(Long userId);

    @Query("SELECT r FROM GithubRepository r WHERE r.user.id = :userId " +
            "AND (:language IS NULL OR r.language = :language)")
    Page<GithubRepository> findByUserIdWithFilter(@Param("userId") Long userId, @Param("language") String language, Pageable pageable);

    @Query("SELECT r FROM GithubRepository r WHERE r.user.id = :userId " +
            "ORDER BY r.stars DESC")
    List<GithubRepository> findTop5ByUserId(@Param("userId") Long userId, Pageable pageable);

    // Projection query — only fetches 6 columns instead of 14
    @Query("SELECT r.githubRepoId AS githubRepoId, r.repoName AS repoName, " +
            "r.language AS language, r.stars AS stars, " +
            "r.forks AS forks, r.repoUrl AS repoUrl " +
            "FROM GithubRepository r WHERE r.user.id = :userId " +
            "ORDER BY r.stars DESC")
    List<RepoProjection> findProjectedByUserId(@Param("userId") Long userId);

    // Aggregation by language — grouped statistics
    @Query("SELECT new com.example.springboot_learning.model.dto.response.LanguageStatsResponse(" +
            "r.language, COUNT(r), SUM(r.stars), SUM(r.forks)) " +
            "FROM GithubRepository r " +
            "WHERE r.user.id = :userId AND r.language IS NOT NULL " +
            "GROUP BY r.language " +
            "ORDER BY COUNT(r) DESC")
    List<LanguageStatsResponse> findLanguageStats(@Param("userId") Long userId);

    // Search by repo name keyword
    @Query("SELECT r FROM GithubRepository r " +
            "WHERE r.user.id = :userId " +
            "AND LOWER(r.repoName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<GithubRepository> searchByRepoName(
            @Param("userId") Long userId,
            @Param("keyword") String keyword);
}