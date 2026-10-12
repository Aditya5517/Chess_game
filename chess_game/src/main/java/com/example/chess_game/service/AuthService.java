package com.example.chess_game.service;

import com.example.chess_game.dto.*;
import com.example.chess_game.entity.UserEntity;
import com.example.chess_game.repository.UserRepository;
import com.example.chess_game.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.username().length() > 100
                || request.email() == null
                || request.email().isBlank()
                || request.email().length() > 200
                || request.password() == null
                || request.password().length() < 8
                || request.password().length() > 72) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid registration details"
            );
        }

        String username = request.username().trim();

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid email"
            );
        }

        if (userRepository.existsByEmail(email)
                || userRepository.existsByUsername(username)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists"
            );
        }

        String hashedPassword = passwordEncoder.encode(
                request.password()
        );

        UserEntity user = new UserEntity(
                username,
                email,
                hashedPassword
        );

        UserEntity savedUser = userRepository.save(user);

        return new AuthResponse(
                jwtService.generateToken(savedUser),
                "Bearer"
        );
    }

    public AuthResponse login(LoginRequest request) {

        if (request == null
                || request.email() == null
                || request.password() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer"
        );
    }
}