package com.example.resourcecenter.controller;

import com.example.resourcecenter.dto.LoginRequest;
import com.example.resourcecenter.dto.RegisterRequest;
import com.example.resourcecenter.entity.User;
import com.example.resourcecenter.entity.VerificationToken;
import com.example.resourcecenter.repository.UserRepository;
import com.example.resourcecenter.repository.VerificationTokenRepository;
import com.example.resourcecenter.security.JwtUtil;
import com.example.resourcecenter.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName()
            );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "На пошту відправлено лист з підтвердженням"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());

            return ResponseEntity.ok().body(Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "role", user.getRole()
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Токен не знайдено"));
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Токен прострочено"));
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Обліковий запис підтверджено"));
    }
}
