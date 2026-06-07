package com.example.springboot_learning.repository;

import com.example.springboot_learning.model.entity.GithubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubRepositoryRepository extends JpaRepository<GithubRepository,Long> {
   List<GithubRepository> findByUserId(Long id);
   Optional<GithubRepository> findByGithubRepoId(Long id);


}
