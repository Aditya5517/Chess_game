package com.example.chess_game.pieces;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;

public class PieceFactory {
    public static Piece createPiece(PieceType type, Color color) {
        if (type == null || color == null) {
            throw new IllegalArgumentException("Type and color cannot be null");
        }
        switch (type) {
            case KING:
                return new King(color);
            case QUEEN:
                return new Queen(color);
            case ROOK:
                return new Rook(color);
            case BISHOP:
                return new Bishop(color);
            case KNIGHT:
                return new Knight(color);
            case PAWN:
                return new Pawn(color);
            default:
                throw new IllegalArgumentException("Invalid piece type");
        }
    }
}