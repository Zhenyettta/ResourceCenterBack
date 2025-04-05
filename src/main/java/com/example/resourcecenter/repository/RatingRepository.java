package com.example.resourcecenter.repository;

import com.example.resourcecenter.entity.Rating;
import com.example.resourcecenter.entity.Resource;
import com.example.resourcecenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndResource(User user, Resource resource);

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.resource.id = :resourceId")
    double findAverageRatingForResource(@Param("resourceId") Long resourceId);
}
