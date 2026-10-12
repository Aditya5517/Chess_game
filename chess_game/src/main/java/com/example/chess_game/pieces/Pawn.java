package com.example.chess_game.pieces;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.*;
import com.example.chess_game.model.Position;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
    }

    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {

        if (from == null || to == null || board == null) {
            return false;
        }

        int direction = getColor() == Color.WHITE ? 1 : -1;
        int startRow = getColor() == Color.WHITE ? 1 : 6;

        int rowDiff = to.getRow() - from.getRow();
        int colDiff = Math.abs(to.getColumn() - from.getColumn());

        // Move one square forward
        if (colDiff == 0 && rowDiff == direction) {
            return !board.isOccupied(to);
        }

        // Move two squares from starting row
        if (colDiff == 0 && rowDiff == 2 * direction
                && from.getRow() == startRow) {

            Position middle = new Position(
                    from.getRow() + direction,
                    from.getColumn()
            );

            return !board.isOccupied(middle)
                    && !board.isOccupied(to);
        }

        // Capture diagonally
        if (colDiff == 1 && rowDiff == direction) {

            return board.isOccupied(to)
                    && canMoveTo(to, board);
        }

        return false;
    }
}