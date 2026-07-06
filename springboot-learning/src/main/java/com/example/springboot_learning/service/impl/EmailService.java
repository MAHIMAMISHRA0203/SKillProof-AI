package com.example.springboot_learning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Async
    public void sendWelcomeEmail(String toEmail, String name){
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to SkillProof AI");
            message.setText(String.format("""
                    Hi %s,
                    
                    Welcome to SkillProof AI! 🎉
                    
                    Your account has been created successfully.
                    
                    Here's what you can do next:
                    1. Connect your GitHub account
                    2. Sync your repositories
                    3. Get your AI-powered skill score
                    4. Share your verified profile with recruiters
                    
                    Get started: http://localhost:8080/swagger-ui/index.html
                    
                    Best regards,
                    SkillProof AI Team
                    """, name));
            mailSender.send(message);

            log.info("Welcome email sent to: {}", toEmail);

        }catch(Exception e){
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());

        }
    }
    @Async

    public void sendScoreReadyEmail(String toEmail,String name,int overallScore,String grade){
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(name);
            message.setSubject("Your  skillProof AI Score is Ready");
            message.setText(String.format("""
                    Hi %s,
                    
                    Your skill analysis is complete! Here are your results:
                    
                    Overall Score: %d/100
                    Grade: %s
                    
                    View your full profile and detailed breakdown:
                    http://localhost:8080/api/v1/skills/score
                    
                    Share your verified profile with recruiters:
                    http://localhost:8080/api/v1/recruiter/%s/profile
                    
                    Keep coding and improving your score! 💪
                    
                    Best regards,
                    SkillProof AI Team
                    """, name, overallScore, grade, name));
            mailSender.send(message);
            log.info("Score ready email sent to: {}", toEmail);

        }catch (Exception e){
            log.error("Failed to send score email to {}: {}", toEmail, e.getMessage());

        }

    }
    @Async
    public void sendAiInsightsReadyEmail(String toEmail, String name,
                                         String skillSummary) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your AI Skill Summary is Ready! 🤖");
            message.setText(String.format("""
                    Hi %s,
                    
                    Your AI-powered skill analysis is complete!
                    
                    Here's what our AI says about you:
                    
                    "%s"
                    
                    View your complete profile:
                    http://localhost:8080/api/v1/ai/insights
                    
                    Best regards,
                    SkillProof AI Team
                     """, name, skillSummary));

                                mailSender.send(message);
                                log.info("AI insights email sent to: {}", toEmail);
                            } catch (Exception e) {
                                log.error("Failed to send AI insights email to {}: {}", toEmail, e.getMessage());
                            }

}}
