package com.example.resourcecenter.util;


import com.example.resourcecenter.dto.ResourceDto;

import com.example.resourcecenter.entity.Comment;
import com.example.resourcecenter.entity.Resource;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ResourceMapper {

    public ResourceDto toDto(Resource resource) {
        return ResourceDto.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .active(resource.isActive())
                .createdAt(resource.getCreatedAt())
                .averageRating(resource.getAverageRating())
                .author(ResourceDto.AuthorDto.builder()
                        .firstName(resource.getAuthor().getFirstName())
                        .lastName(resource.getAuthor().getLastName())
                        .build())
                .comments(toCommentDtos(resource.getComments()))
                .build();
    }

    private List<ResourceDto.CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream().map(comment ->
                ResourceDto.CommentDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .author(ResourceDto.AuthorDto.builder()
                                .firstName(comment.getAuthor().getFirstName())
                                .lastName(comment.getAuthor().getLastName())
                                .build())
                        .build()
        ).collect(Collectors.toList());
    }
}