package com.example.resourcecenter.repository;

import com.example.resourcecenter.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByResourceId(Long resourceId);
    Page<Comment> findByResourceId(Long resourceId, Pageable pageable);
}