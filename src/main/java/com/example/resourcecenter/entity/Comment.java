package com.example.resourcecenter.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User author;

    @ManyToOne
    private Resource resource;

    @Column(length = 500)
    private String content;

    private LocalDateTime createdAt;
}