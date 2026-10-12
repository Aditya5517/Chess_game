package com.example.chess_game.pieces;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.*;
import com.example.chess_game.model.Position;

public class King extends Piece {

    public King(Color color) {
        super(color);
    }

    @Override
    public PieceType getType() {
        return PieceType.KING;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {

        if (from == null || to == null || board == null) {
            return false;
        }

        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getColumn() - from.getColumn());

        boolean validPattern = rowDiff <= 1 && colDiff <= 1
                && (rowDiff != 0 || colDiff != 0);

        return validPattern && canMoveTo(to, board);
    }
}