package com.example.resourcecenter.service;

import com.example.resourcecenter.entity.Resource;
import com.example.resourcecenter.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository repository;

    public Resource create(Resource resource) {
        resource.setCreatedAt(LocalDateTime.now());
        resource.setActive(true);
        resource.setRating(0);
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


    public Resource getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }


    public Resource updateRating(Long id, int rating) {
        return repository.findById(id)
                .map(resource -> {
                    resource.setRating(rating);
                    return repository.save(resource);
                })
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }
}
