
package com.example.chess_game.dto;

import com.example.chess_game.enums.MoveType;

public record LegalMoveResponse(
        String position,
        MoveType moveType,
        boolean capture
) {
}
