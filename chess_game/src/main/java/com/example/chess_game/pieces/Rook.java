package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }
    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }
}