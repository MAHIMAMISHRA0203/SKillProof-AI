package com.example.springboot_learning;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.SkillScore.ScoreStatus;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import com.example.springboot_learning.repository.SkillScoreRepository;
import com.example.springboot_learning.service.impl.SkillScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillscoringServiceTest {

    @Mock
    private SkillScoreRepository skillScoreRepository;

    @Mock
    private GithubRepositoryRepository githubRepositoryRepository;

    @InjectMocks
    private SkillScoringService skillScoringService;

    private User testUser;
    private SkillScore testScore;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Mahima")
                .email("mahima@gmail.com")
                .build();

        testScore = SkillScore.builder()
                .id(1L)
                .user(testUser)
                .status(ScoreStatus.PENDING)
                .build();
    }

    // ── analyzeScore tests ───────────────────────────────────────────

    @Test
    @DisplayName("analyzeScore: should throw when no repos found")
    void analyzeScore_shouldThrow_whenNoReposFound() {
        // ARRANGE
        when(githubRepositoryRepository.findByUserId(1L))
                .thenReturn(List.of()); // empty list

        // ACT + ASSERT
        assertThrows(
                CustomException.GeneralException.class,
                () -> skillScoringService.analyzeScore(testUser)
        );
    }

    @Test
    @DisplayName("analyzeScore: should compute COMPLETED score when repos exist")
    void analyzeScore_shouldComputeScore_whenReposExist() {
        // ARRANGE — build 3 test repos
        GithubRepository repo1 = GithubRepository.builder()
                .repoName("spring-project")
                .language("Java")
                .description("A spring boot project")
                .pushedAt(LocalDateTime.now().minusDays(10)) // recent
                .build();

        GithubRepository repo2 = GithubRepository.builder()
                .repoName("python-scripts")
                .language("Python")
                .description(null) // no description
                .pushedAt(LocalDateTime.now().minusDays(5)) // recent
                .build();

        GithubRepository repo3 = GithubRepository.builder()
                .repoName("old-project")
                .language("Java")
                .description("Old project")
                .pushedAt(LocalDateTime.now().minusMonths(8)) // NOT recent
                .build();

        when(githubRepositoryRepository.findByUserId(1L))
                .thenReturn(List.of(repo1, repo2, repo3));
        when(skillScoreRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(testScore));
        when(skillScoreRepository.save(any(SkillScore.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // thenAnswer returns whatever was passed in — simulates save()

        // ACT
        SkillScore result = skillScoringService.analyzeScore(testUser);

        // ASSERT
        assertThat(result.getStatus()).isEqualTo(ScoreStatus.COMPLETED);
        assertThat(result.getConsistencyScore()).isEqualTo(20); // 2 recent repos * 10
        assertThat(result.getDiversityScore()).isEqualTo(20);   // 2 languages * 10
        assertThat(result.getDocumentationScore()).isEqualTo(66); // 2 out of 3 have description
        assertThat(result.getGrade()).isNotNull();
        verify(skillScoreRepository, times(2)).save(any(SkillScore.class));
        // called twice: once for PROCESSING, once for COMPLETED
    }

    // ── getScore tests ───────────────────────────────────────────────

    @Test
    @DisplayName("getScore: should return score when found")
    void getScore_shouldReturnScore_whenFound() {
        // ARRANGE
        testScore.setStatus(ScoreStatus.COMPLETED);
        testScore.setOverallScore(75);
        testScore.setGrade("B+");

        when(skillScoreRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(testScore));

        // ACT
        SkillScore result = skillScoringService.getScore(testUser);

        // ASSERT
        assertThat(result.getOverallScore()).isEqualTo(75);
        assertThat(result.getGrade()).isEqualTo("B+");
        assertThat(result.getStatus()).isEqualTo(ScoreStatus.COMPLETED);
    }

    @Test
    @DisplayName("getScore: should throw when score not found")
    void getScore_shouldThrow_whenScoreNotFound() {
        // ARRANGE
        when(skillScoreRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                CustomException.ResourceNotFoundException.class,
                () -> skillScoringService.getScore(testUser)
        );
    }

    // ── initializeScore tests ────────────────────────────────────────

    @Test
    @DisplayName("initializeScore: should return existing score if already exists")
    void initializeScore_shouldReturnExisting_whenAlreadyExists() {
        // ARRANGE
        when(skillScoreRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(testScore));

        // ACT
        SkillScore result = skillScoringService.initializeScore(testUser);

        // ASSERT
        assertThat(result).isEqualTo(testScore);
        verify(skillScoreRepository, never()).save(any()); // should NOT create new one
    }

    @Test
    @DisplayName("initializeScore: should create new score if none exists")
    void initializeScore_shouldCreateNew_whenNoneExists() {
        // ARRANGE
        when(skillScoreRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());
        when(skillScoreRepository.save(any(SkillScore.class)))
                .thenReturn(testScore);

        // ACT
        SkillScore result = skillScoringService.initializeScore(testUser);

        // ASSERT
        verify(skillScoreRepository, times(1)).save(any(SkillScore.class));
        assertThat(result.getStatus()).isEqualTo(ScoreStatus.PENDING);
    }
}