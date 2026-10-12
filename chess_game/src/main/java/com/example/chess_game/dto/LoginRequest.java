package com.example.chess_game.dto;

public record LoginRequest(
        String email,
        String password
) {
}