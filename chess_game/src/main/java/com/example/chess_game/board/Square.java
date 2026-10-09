package com.example.chess_game.board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;

public class Square {
    private Position position;
    private Color squareColor;
    private Piece piece;
    public Square(Position position) {
        this.position = position;
        int row = position.getRow();
        int column = position.getColumn();
        if ((row + column) % 2 == 0) {
            squareColor = Color.BLACK;
        } else {
            squareColor = Color.WHITE;
        }
        piece = null;
    }
    public Position getPosition() {
        return position;
    }
    public Color getSquareColor() {
        return squareColor;
    }
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public boolean isEmpty() {
        return piece == null;
    }
}