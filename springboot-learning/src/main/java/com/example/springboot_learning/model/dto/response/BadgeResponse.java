package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BadgeResponse {
    private String name;
    private String description;
    private String emoji;
    private String category;


}
