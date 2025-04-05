package com.example.resourcecenter.repository;

import com.example.resourcecenter.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllByActiveTrue();

    Page<Resource> findByActiveTrue(Pageable pageable);

    Page<Resource> findByActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
    SELECT r FROM Resource r
    WHERE (:keyword IS NULL OR :keyword = '' OR
           LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:activeOnly = false OR r.active = true)
""")
    Page<Resource> searchResources(
            @Param("keyword") String keyword,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable
    );




}
