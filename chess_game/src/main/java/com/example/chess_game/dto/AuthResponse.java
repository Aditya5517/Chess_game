package com.example.chess_game.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
}