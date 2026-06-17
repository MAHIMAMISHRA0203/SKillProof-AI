package com.example.springboot_learning.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroqRequest {
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("max_Tokens")
    private Integer maxTokens;

    @JsonProperty("temperature")
    private Double temperature;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message{
        @JsonProperty("role")
        private String role;
        @JsonProperty("content")
        private String content;
    }



}
