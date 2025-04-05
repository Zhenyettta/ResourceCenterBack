package com.example.resourcecenter.dto;

import com.example.resourcecenter.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    private String title;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private double averageRating;
    private List<CommentDto> comments;
    private AuthorDto author;
    private List<Rating> rating;

    @Data
    @AllArgsConstructor
    @Builder
    public static class CommentDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private AuthorDto author;

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class AuthorDto {
        private String firstName;
        private String lastName;
    }
}