package com.example.chess_game.pieces;

import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.model.Position;
import com.example.chess_game.board.Board;

public abstract class Piece {

    private Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract PieceType getType();

    public abstract boolean isValidMove(
            Position from,
            Position to,
            Board board
    );
    protected boolean canMoveTo(Position to, Board board) {

        Piece destinationPiece = board.getSquare(to).getPiece();
        if (destinationPiece == null) {
            return true;
        }
        return destinationPiece.getColor() != this.color;
    }
}