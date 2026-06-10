package com.example.springboot_learning.repository;

import com.example.springboot_learning.model.entity.GithubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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



}
