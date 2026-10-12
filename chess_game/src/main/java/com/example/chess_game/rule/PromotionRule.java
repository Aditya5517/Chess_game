package com.example.chess_game.rule;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;

public class PromotionRule implements GameRule {

    @Override
    public boolean isValid(Move move, Board board, MoveHistory history) {

        Position from = move.getFrom();
        Position to = move.getTo();

        Piece pawn = board.getSquare(from).getPiece();

        if (pawn == null || pawn.getType() != PieceType.PAWN) {
            return false;
        }

        PieceType promotionType = move.getPromotionType();

        if (promotionType != PieceType.QUEEN
                && promotionType != PieceType.ROOK
                && promotionType != PieceType.BISHOP
                && promotionType != PieceType.KNIGHT) {
            return false;
        }

        Color color = pawn.getColor();

        int direction = color == Color.WHITE ? 1 : -1;
        int lastRow = color == Color.WHITE ? 7 : 0;

        if (to.getRow() != lastRow) {
            return false;
        }

        if (to.getRow() - from.getRow() != direction) {
            return false;
        }

        int columnDifference = Math.abs(
                to.getColumn() - from.getColumn()
        );

        Piece destination = board.getSquare(to).getPiece();

        if (columnDifference == 0) {
            return destination == null;
        }

        if (columnDifference == 1) {
            return destination != null
                    && destination.getColor() != color
                    && destination.getType() != PieceType.KING;
        }

        return false;
    }
}