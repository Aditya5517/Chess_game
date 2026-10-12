package com.example.chess_game.validator;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;

public interface MoveValidator {

    boolean isValid(
            Move move,
            Board board,
            Color currentTurn,
            MoveHistory history
    );
}