package com.example.chess_game.rule;

import com.example.chess_game.board.Board;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;

public interface GameRule {

    boolean isValid(
            Move move,
            Board board,
            MoveHistory history
    );
}