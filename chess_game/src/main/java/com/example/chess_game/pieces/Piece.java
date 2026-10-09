package com.example.chess_game.pieces;

import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
public abstract class Piece {
    private Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    public abstract PieceType getType();
}