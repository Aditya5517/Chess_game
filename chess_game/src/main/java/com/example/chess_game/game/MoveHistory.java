package com.example.chess_game.game;

import com.example.chess_game.model.Move;

import java.util.ArrayList;
import java.util.List;

public class MoveHistory {
    private List<Move> moves;
    public MoveHistory() {
        moves = new ArrayList<>();
    }
    public void add(Move move) {
        moves.add(move);
    }
    public Move undo() {
        if (moves.isEmpty()) {
            return null;
        }
        return moves.remove(moves.size() - 1);
    }
    public List<Move> getMoves() {
        return new ArrayList<>(moves);
    }
}