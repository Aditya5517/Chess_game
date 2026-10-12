package com.example.chess_game.pieces;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.*;
import com.example.chess_game.model.Position;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color);
    }

    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {

        if (from == null || to == null || board == null) {
            return false;
        }

        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getColumn() - from.getColumn();

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        boolean validPattern = rowDiff == 0 || colDiff == 0;

        if (!validPattern) {
            return false;
        }

        return board.isPathClear(from, to)
                && canMoveTo(to, board);
    }
}