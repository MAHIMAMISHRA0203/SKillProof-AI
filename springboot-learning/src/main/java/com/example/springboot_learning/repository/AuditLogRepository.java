package com.example.springboot_learning.repository;

import com.example.springboot_learning.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog>findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AuditLog>findByActionOrderByCreatedAtDesc(String action);


}
