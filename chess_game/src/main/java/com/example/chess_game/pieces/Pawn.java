package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }
    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }
}