package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }
    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }
}