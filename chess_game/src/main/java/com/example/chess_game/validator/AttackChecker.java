package com.example.chess_game.validator;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;

public class AttackChecker {

    public boolean isAttacked(
            Position target,
            Color attackingColor,
            Board board
    ) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position from = new Position(row, col);
                Piece piece = board.getSquare(from).getPiece();

                if (piece == null || piece.getColor() != attackingColor) {
                    continue;
                }

                if (piece.getType() == PieceType.PAWN) {

                    int direction = attackingColor == Color.WHITE ? 1 : -1;

                    if (target.getRow() - row == direction
                            && Math.abs(target.getColumn() - col) == 1) {
                        return true;
                    }

                } else if (piece.isValidMove(from, target, board)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isKingInCheck(Color color, Board board) {

        Position kingPosition = null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position position = new Position(row, col);
                Piece piece = board.getSquare(position).getPiece();

                if (piece != null
                        && piece.getType() == PieceType.KING
                        && piece.getColor() == color) {

                    kingPosition = position;
                    break;
                }
            }

            if (kingPosition != null) {
                break;
            }
        }

        if (kingPosition == null) {
            return true;
        }

        Color opponent = color == Color.WHITE
                ? Color.BLACK : Color.WHITE;

        return isAttacked(kingPosition, opponent, board);
    }
}