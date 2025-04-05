package com.example.resourcecenter.service;

import com.example.resourcecenter.entity.Role;
import com.example.resourcecenter.entity.User;
import com.example.resourcecenter.entity.VerificationToken;
import com.example.resourcecenter.repository.UserRepository;
import com.example.resourcecenter.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final MailtrapMailService mailtrapMailService;

    public void register(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Користувач з таким email вже існує");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(hashedPassword)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(false)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        sendVerificationEmail(savedUser);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Акаунт не підтверджено. Перевірте email.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Невірний пароль");
        }

        return user;
    }

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();


        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();


        tokenRepository.save(verificationToken);

        String link = "http://localhost:8080/api/auth/confirm?token=" + token;

        mailtrapMailService.sendVerification(user.getEmail(), link);
    }

}
