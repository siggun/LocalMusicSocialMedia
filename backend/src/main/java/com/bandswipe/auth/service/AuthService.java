package com.bandswipe.auth.service;

import com.bandswipe.auth.dto.AuthResponse;
import com.bandswipe.auth.dto.GoogleAuthRequest;
import com.bandswipe.auth.dto.LoginRequest;
import com.bandswipe.auth.dto.RegisterRequest;
import com.bandswipe.auth.entity.User;
import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .authProvider(AuthProvider.EMAIL)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        User user = userRepository.findByFirebaseUid(request.firebaseToken())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(request.email())
                            .firebaseUid(request.firebaseToken())
                            .displayName(request.displayName())
                            .authProvider(AuthProvider.GOOGLE)
                            .isActive(true)
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    public AuthResponse refreshToken(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
