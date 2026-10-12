package com.example.chess_game.game;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;

public interface GameStateEvaluator {

    GameStatus evaluate(
            Board board,
            Color currentTurn,
            MoveHistory history
    );
}