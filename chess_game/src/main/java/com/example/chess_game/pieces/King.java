package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public class King extends Piece {
    public King(Color color) {
        super(color);
    }
    @Override
    public PieceType getType() {
        return PieceType.KING;
    }
}