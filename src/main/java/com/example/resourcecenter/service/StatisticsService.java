package com.example.resourcecenter.service;

import com.example.resourcecenter.dto.StatisticsDto;
import com.example.resourcecenter.entity.Rating;
import com.example.resourcecenter.repository.CommentRepository;
import com.example.resourcecenter.repository.RatingRepository;
import com.example.resourcecenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public StatisticsDto getStatistics() {
        long userCount = userRepository.count();


        double averageCommentsPerUser = userCount == 0 ? 0.0 : (double) commentRepository.count() / userCount;


        Map<Integer, Long> ratingDistribution = ratingRepository.findAll().stream()
                .collect(Collectors.groupingBy(Rating::getValue, Collectors.counting()));


        DoubleSummaryStatistics stats = ratingRepository.findAll().stream()
                .collect(Collectors.summarizingDouble(Rating::getValue));

        return StatisticsDto.builder()
                .averageCommentsPerUser(averageCommentsPerUser)
                .ratingDistribution(ratingDistribution)
                .averageRating(stats.getAverage())
                .build();
    }
}
