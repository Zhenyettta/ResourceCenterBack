package com.example.resourcecenter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIncludeProperties({"firstName", "lastName"})
    private User author;

    @ManyToOne
    @JsonIgnore
    private Resource resource;

    @Column(length = 500)
    private String content;

    private LocalDateTime createdAt;
}