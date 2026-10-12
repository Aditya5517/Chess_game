package com.example.chess_game.rule;
import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.validator.AttackChecker;

public class CastlingRule implements GameRule {

    private final AttackChecker attackChecker = new AttackChecker();

    @Override
    public boolean isValid(Move move, Board board, MoveHistory history) {

        Position from = move.getFrom();
        Position to = move.getTo();

        Piece king = board.getSquare(from).getPiece();

        if (king == null || king.getType() != PieceType.KING) {
            return false;
        }

        Color color = king.getColor();
        int row = color == Color.WHITE ? 0 : 7;

        if (from.getRow() != row || from.getColumn() != 4) {
            return false;
        }

        if (to.getRow() != row) {
            return false;
        }

        boolean shortCastle = to.getColumn() == 6;
        boolean longCastle = to.getColumn() == 2;

        if (!shortCastle && !longCastle) {
            return false;
        }

        int rookColumn = shortCastle ? 7 : 0;

        Position rookPosition = new Position(row, rookColumn);
        Piece rook = board.getSquare(rookPosition).getPiece();

        if (rook == null
                || rook.getType() != PieceType.ROOK
                || rook.getColor() != color) {
            return false;
        }

        for (Move previous : history.getMoves()) {

            if (previous.getPiece() == king
                    || previous.getPiece() == rook) {
                return false;
            }
        }

        int start = shortCastle ? 5 : 1;
        int end = shortCastle ? 6 : 3;

        for (int col = start; col <= end; col++) {

            if (board.isOccupied(new Position(row, col))) {
                return false;
            }
        }

        Color opponent = color == Color.WHITE
                ? Color.BLACK : Color.WHITE;

        if (attackChecker.isAttacked(from, opponent, board)) {
            return false;
        }

        int middleColumn = shortCastle ? 5 : 3;

        Position middle = new Position(row, middleColumn);

        board.getSquare(from).setPiece(null);
        board.getSquare(middle).setPiece(king);

        boolean middleAttacked;

        try {
            middleAttacked = attackChecker.isAttacked(
                    middle, opponent, board
            );
        } finally {
            board.getSquare(middle).setPiece(null);
            board.getSquare(from).setPiece(king);
        }

        return !middleAttacked;
    }
}