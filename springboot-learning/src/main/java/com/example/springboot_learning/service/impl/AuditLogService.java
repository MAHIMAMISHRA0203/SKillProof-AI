package com.example.springboot_learning.service.impl;

import com.example.springboot_learning.model.entity.AuditLog;
import com.example.springboot_learning.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class AuditLogService {
    private final AuditLogRepository auditRepository;
    @Async
    public void log(Long userId, String userEmail, String action, String details, AuditLog.AuditStatus status){
        try{
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .action(action)
                    .details(details)
                    .status(status)
                    .build();
            auditRepository.save(auditLog);
            log.info("Audit log saved: {} - {} - {}", userEmail, action, status);

        }catch(Exception  e){
            log.error("Failed to save audit log: {}", e.getMessage());

        }
    }
    public List<AuditLog> getUserLogs(Long userId) {
        return auditRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

}
