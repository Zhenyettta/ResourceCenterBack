package com.example.resourcecenter.controller;

import com.example.resourcecenter.entity.Resource;
import com.example.resourcecenter.entity.User;
import com.example.resourcecenter.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService service;

    @PostMapping
    public ResponseEntity<Resource> create(@RequestBody Resource resource, @AuthenticationPrincipal User user) {
        resource.setAuthor(user);
        return ResponseEntity.ok(service.create(resource));
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getAll() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> update(@PathVariable Long id, @RequestBody Resource resource) {
        return ResponseEntity.ok(service.update(id, resource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Resource> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<Resource> updateRating(@PathVariable Long id, @RequestParam int rating) {
        return ResponseEntity.ok(service.updateRating(id, rating));
    }
}