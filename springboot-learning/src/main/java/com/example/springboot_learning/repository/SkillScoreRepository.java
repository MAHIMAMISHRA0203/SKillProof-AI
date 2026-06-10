package com.example.springboot_learning.repository;

import com.example.springboot_learning.model.entity.SkillScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  SkillScoreRepository  extends JpaRepository<SkillScore,Long>
{
    Optional< SkillScore> findUserById(Long userId);
}
