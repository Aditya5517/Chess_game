package com.example.chess_game.pieces;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.*;
import com.example.chess_game.model.Position;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color);
    }

    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {

        if (from == null || to == null || board == null) {
            return false;
        }

        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getColumn() - from.getColumn());

        boolean validPattern =
                (rowDiff == 2 && colDiff == 1)
                        || (rowDiff == 1 && colDiff == 2);

        return validPattern && canMoveTo(to, board);
    }
}