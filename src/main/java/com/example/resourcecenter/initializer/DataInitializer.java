package com.example.resourcecenter.initializer;

import com.example.resourcecenter.entity.*;
import com.example.resourcecenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceRepository resourceRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        User admin = createUserIfNotExists("admin@example.com", "admin", Role.ADMIN, "Admin", "Adminovich");
        User user = createUserIfNotExists("user@example.com", "user", Role.USER, "User", "Userson");
        List<User> users = new ArrayList<>(List.of(admin));

        // ➕ Додаткові користувачі
        for (int i = 1; i <= 10; i++) {
            users.add(createUserIfNotExists(
                    "user" + i + "@example.com",
                    "password" + i,
                    Role.USER,
                    "User" + i,
                    "Lastname" + i
            ));
        }

        // ➕ Створення ресурсів, коментарів та оцінок
        if (resourceRepository.count() == 0) {
            List<Resource> resources = new ArrayList<>();

            for (int i = 1; i <= 20; i++) {
                User author = users.get(random.nextInt(users.size()));
                Resource res = new Resource();
                res.setTitle("Ресурс №" + i);
                res.setDescription("Опис ресурсу номер " + i + ". Це демонстраційний ресурс.");
                res.setAuthor(author);
                res.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(100)));
                res.setActive(true);
                res.setAverageRating(0);
                resources.add(resourceRepository.save(res));
            }

            // ➕ Коментарі
            List<Comment> allComments = new ArrayList<>();
            for (Resource res : resources) {
                int commentCount = 2 + random.nextInt(4);
                for (int j = 0; j < commentCount; j++) {
                    User commenter = users.get(random.nextInt(users.size()));
                    allComments.add(Comment.builder()
                            .resource(res)
                            .author(commenter)
                            .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                            .content("Коментар " + (j + 1) + " до \"" + res.getTitle() + "\"")
                            .build());
                }
            }
            commentRepository.saveAll(allComments);

            // ➕ Оцінки
            List<Rating> allRatings = new ArrayList<>();
            for (Resource res : resources) {
                Set<Long> ratedUserIds = new HashSet<>();
                int ratingCount = 1 + random.nextInt(5);
                double total = 0;

                for (int j = 0; j < ratingCount; j++) {
                    User rater = users.get(random.nextInt(users.size()));
                    if (ratedUserIds.contains(rater.getId())) continue;

                    int val = 1 + random.nextInt(5);
                    total += val;
                    ratedUserIds.add(rater.getId());

                    allRatings.add(Rating.builder()
                            .user(rater)
                            .resource(res)
                            .value(val)
                            .build());
                }

                if (!ratedUserIds.isEmpty()) {
                    res.setAverageRating(total / ratedUserIds.size());
                    resourceRepository.save(res);
                }
            }

            ratingRepository.saveAll(allRatings);
            System.out.println("📘 20 ресурсів, коментарі та оцінки додані.");
        }
    }

    private User createUserIfNotExists(String email, String rawPassword, Role role, String firstName, String lastName) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .firstName(firstName)
                    .lastName(lastName)
                    .enabled(true)
                    .build();
            return userRepository.save(user);
        });
    }
}
