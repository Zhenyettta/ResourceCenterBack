package com.example.resourcecenter.repository;

import com.example.resourcecenter.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllByActiveTrue();
}
