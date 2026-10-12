package com.example.chess_game.pieces;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.*;
import com.example.chess_game.model.Position;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color);
    }

    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {

        if (from == null || to == null || board == null) {
            return false;
        }

        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getColumn() - from.getColumn());

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        boolean straight = rowDiff == 0 || colDiff == 0;
        boolean diagonal = rowDiff == colDiff;

        if (!straight && !diagonal) {
            return false;
        }

        return board.isPathClear(from, to)
                && canMoveTo(to, board);
    }
}