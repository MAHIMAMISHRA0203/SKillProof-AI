package com.example.springboot_learning.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroqResponse {

    @JsonProperty("choices")
    private List<Choice> choices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        @JsonProperty("message")
        private Message message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("content")
        private String content;
    }

    public String getContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent();
        }
        return "";
    }
}