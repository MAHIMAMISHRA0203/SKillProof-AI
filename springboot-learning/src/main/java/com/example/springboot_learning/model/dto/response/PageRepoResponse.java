package com.example.springboot_learning.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
public class PageRepoResponse {
    private
    List<RepoItemResponse> repos;
    private int currentPage;
    private int totalPages;
    private long totalRepos;
    private boolean hasNext;
    private boolean hasPrevious;



}
