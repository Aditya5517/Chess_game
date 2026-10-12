package com.example.chess_game.dto;

import com.example.chess_game.enums.GameStatus;

public record GameResponse(
        Long id,
        String whitePlayer,
        String blackPlayer,
        GameStatus status,
        String winner
) {
}