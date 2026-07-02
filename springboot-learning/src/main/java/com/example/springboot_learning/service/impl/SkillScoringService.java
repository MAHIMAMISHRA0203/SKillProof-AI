package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.exception.CustomException;
import com.example.springboot_learning.model.entity.AuditLog;
import com.example.springboot_learning.model.entity.GithubRepository;
import com.example.springboot_learning.model.entity.SkillScore;
import com.example.springboot_learning.model.entity.SkillScore.ScoreStatus;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.repository.GithubRepositoryRepository;
import com.example.springboot_learning.repository.SkillScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillScoringService {
    private  final SkillScoreRepository skillScoreRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final AiInsightService aiInsightService;
    private final AuditLogService auditLogService;
    public SkillScore initializeScore(User user) {
        return skillScoreRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    SkillScore score = SkillScore.builder()
                            .user(user)
                            .status(ScoreStatus.PENDING)
                            .build();
                    return skillScoreRepository.save(score);
                });
    }
    @CacheEvict(value = "skillscore", key = "'user_' + #user.id")

    public SkillScore analyzeScore(User user){
        List<GithubRepository> repos=githubRepositoryRepository.findByUserId(user.getId());
        if(repos.isEmpty()){
            throw new CustomException.GeneralException("No repositories found.Please sync your github repo first");

        }
SkillScore score=initializeScore(user);
        score.setStatus(ScoreStatus.PROCESSING);
        skillScoreRepository.save(score);
        int consistency=computeConsistencyScore(repos);
        int diversity=computediversityScore(repos);
        int documentation=computeDocumentationScore(repos);
        int codeQuality=aiInsightService.getSiCOdeQuality(user);
        int overall =(consistency+diversity+documentation+codeQuality)/4;

        score.setConsistencyScore(consistency);
        score.setDiversityScore(diversity);
        score.setDocumentationScore(documentation);
        score.setCodeQualityScore(codeQuality);
        score.setOverallScore(overall);
        score.setGrade(computeGrade(overall));
        score.setStatus(ScoreStatus.COMPLETED);
        auditLogService.log(
                user.getId(),
                user.getEmail(),
                "SKILL_SCORE_COMPUTED",
                "Overall score: " + overall + " Grade: " + computeGrade(overall),
                AuditLog.AuditStatus.SUCCESS
        );
        return skillScoreRepository.save(score);
}

    @Cacheable(value = "skillscore", key = "'user_' + #user.id")


    public SkillScore getScore(User user){
        return skillScoreRepository.findByUser_Id(user.getId())
                .orElseThrow(()->new CustomException.ResourceNotFoundException("No score found for this user"));

}
private int computeConsistencyScore(List<GithubRepository>repos){
    LocalDateTime sixMonthsAgo=LocalDateTime.now().minusMonths(6);
    long recentCount=repos.stream()
            .filter(r->r.getPushedAt()!=null && r.getPushedAt().isAfter(sixMonthsAgo))
            .count();
    return (int) Math.min(recentCount*10,100);
}
private int computediversityScore(List<GithubRepository>repos){
        Set<String> languages=repos.stream()
                .filter(r->r.getLanguage()!=null)
                .map(GithubRepository::getLanguage)
                .collect(Collectors.toSet());
        return Math.min(languages.size()*10,100);
}
private int computeDocumentationScore(List<GithubRepository>repos){
        if(repos.isEmpty())return 0;
        long described=repos.stream()
                .filter(r->r.getDescription()!=null  && !r.getDescription().isEmpty())
                .count();
        return (int)((described*100)/repos.size());
}
private String computeGrade(int score){
    if(score>=90)    return "A+";
    if(score>=80)    return "A";
    if(score>=70)    return "B+";
    if(score>=60)    return "B";
    if(score>=50)    return "C+";
    else   return "D";


}


}
