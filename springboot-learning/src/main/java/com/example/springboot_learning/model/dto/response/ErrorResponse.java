package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private String error;
    private LocalDateTime timestamp;

}
