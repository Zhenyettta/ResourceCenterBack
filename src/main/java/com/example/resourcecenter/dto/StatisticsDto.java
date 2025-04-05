package com.example.resourcecenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class StatisticsDto {
    private double averageCommentsPerUser;
    private Map<Integer, Long> ratingDistribution;
    private double averageRating;
}