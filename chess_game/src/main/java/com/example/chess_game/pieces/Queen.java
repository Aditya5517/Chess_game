package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }
    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }
}