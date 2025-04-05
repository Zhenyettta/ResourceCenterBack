package com.example.resourcecenter.controller;

import com.example.resourcecenter.dto.ResourceDto;
import com.example.resourcecenter.entity.Resource;
import com.example.resourcecenter.entity.User;
import com.example.resourcecenter.service.ResourceService;
import com.example.resourcecenter.util.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService service;

    @PostMapping
    public ResponseEntity<ResourceDto> create(@RequestBody Resource resource, @AuthenticationPrincipal User user) {
        resource.setAuthor(user);
        return ResponseEntity.ok(ResourceMapper.toDto(service.create(resource)));
    }

    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAll() {
        return ResponseEntity.ok(service.getAllActive().stream().map(ResourceMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ResourceMapper.toDto(service.getById(id, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> update(@PathVariable Long id, @RequestBody Resource resource) {
        return ResponseEntity.ok(ResourceMapper.toDto(service.update(id, resource)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ResourceDto> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ResourceMapper.toDto(service.toggleActive(id)));
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<ResourceDto> updateRating(
            @PathVariable Long id,
            @RequestParam int rating,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ResourceMapper.toDto(service.updateRating(id, rating, user)));
    }

    @DeleteMapping("/{resourceId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long resourceId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        service.deleteComment(resourceId, commentId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ResourceDto>> getPagedResources(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        if(Objects.equals(sort, "top")) {
            sort = "averageRating";
        }
        return ResponseEntity.ok(service.search(search, page, size, sort, direction, activeOnly).map(ResourceMapper::toDto));
    }

    @PostMapping("/{resourceId}/comments")
    public ResponseEntity<ResourceDto> addComment(
            @PathVariable Long resourceId,
            @RequestParam String content,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ResourceMapper.toDto(service.addComment(resourceId, content, user)));
    }
}