package com.example.springboot_learning.controller;

import com.example.springboot_learning.model.entity.AuditLog;
import com.example.springboot_learning.model.entity.User;
import com.example.springboot_learning.service.AuthService;
import com.example.springboot_learning.service.impl.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit logs for developer actions")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuthService authService;

    @GetMapping("/my")
    @Operation(summary = "Get my audit logs",
            description = "Returns all actions performed by the current user")
    @PreAuthorize("hasAnyRole('USER', 'RECRUITER')")

    public ResponseEntity<List<AuditLog>> getMyLogs() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(auditLogService.getUserLogs(currentUser.getId()));
    }
}