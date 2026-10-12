package com.example.chess_game.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}