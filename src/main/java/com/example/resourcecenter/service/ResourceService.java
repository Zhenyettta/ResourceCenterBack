package com.example.resourcecenter.service;

import com.example.resourcecenter.entity.*;
import com.example.resourcecenter.repository.CommentRepository;
import com.example.resourcecenter.repository.RatingRepository;
import com.example.resourcecenter.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository repository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;

    public Resource create(Resource resource) {
        resource.setCreatedAt(LocalDateTime.now());
        resource.setActive(true);
        resource.setAverageRating(0);
        return repository.save(resource);
    }

    public List<Resource> getAllActive() {
        return repository.findAllByActiveTrue();
    }

    public Resource update(Long id, Resource updatedResource) {
        return repository.findById(id)
                .map(existingResource -> {
                    existingResource.setTitle(updatedResource.getTitle());
                    existingResource.setDescription(updatedResource.getDescription());
                    // Don't update fields that should be managed separately
                    return repository.save(existingResource);
                })
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }


    public void delete(Long id) {
        repository.deleteById(id);
    }


    public Resource toggleActive(Long id) {
        return repository.findById(id)
                .map(resource -> {
                    resource.setActive(!resource.isActive());
                    return repository.save(resource);
                })
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    public  Resource getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    public  Resource getById(Long id, User currentUser) {
        Resource resource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));


        if (resource.getRatings() != null) {
            resource.getRatings().sort((r1, r2) -> Integer.compare(r2.getValue(), r1.getValue()));
        }

        if (currentUser != null) {
            List<Rating> userRating = resource.getRatings().stream()
                    .filter(rating -> rating.getUser().getId().equals(currentUser.getId()))
                    .toList();

            resource.setRatings(userRating);
        } else {
            // If no user is provided, clear all ratings from the response
            resource.setRatings(new ArrayList<>());
        }

        return resource;
    }


    public Resource updateRating(Long resourceId, int rating, User user) {
        Resource resource = repository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        Rating existing = ratingRepository.findByUserAndResource(user, resource).orElse(null);

        if (existing == null) {
            Rating newRating = Rating.builder()
                    .user(user)
                    .resource(resource)
                    .value(rating)
                    .build();
            ratingRepository.save(newRating);
        } else {
            existing.setValue(rating);
            ratingRepository.save(existing);
        }


        double average = ratingRepository.findAverageRatingForResource(resourceId);
        resource.setAverageRating(average);

        return repository.save(resource);
    }


    public void deleteComment(Long resourceId, Long commentId, User user) {
        Resource resource = repository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        Comment comment = resource.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can delete comments");
        }

        resource.getComments().remove(comment);
        commentRepository.delete(comment);
    }


    public Page<Resource> search(String keyword, int page, int size, String sortBy, String direction, boolean activeOnly) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.searchResources(keyword, activeOnly, pageable);
    }


    public Resource addComment(Long resourceId, String content, User user) {
        Resource resource = getById(resourceId);

        Comment comment = Comment.builder()
                .content(content)
                .author(user)
                .resource(resource)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
        resource.getComments().add(comment);
        return resource;
    }
}
