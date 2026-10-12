
package com.example.chess_game.rule;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;

import java.util.List;

public class EnPassantRule implements GameRule {

    @Override
    public boolean isValid(Move move, Board board, MoveHistory history) {

        if (move == null || board == null || history == null) {
            return false;
        }

        Position from = move.getFrom();
        Position to = move.getTo();

        if (from == null || to == null) {
            return false;
        }

        Piece pawn = board.getSquare(from).getPiece();

        if (pawn == null || pawn.getType() != PieceType.PAWN) {
            return false;
        }

        Color color = pawn.getColor();

        int direction = color == Color.WHITE ? 1 : -1;
        int requiredRow = color == Color.WHITE ? 4 : 3;

        if (from.getRow() != requiredRow) {
            return false;
        }

        if (to.getRow() - from.getRow() != direction) {
            return false;
        }

        if (Math.abs(to.getColumn() - from.getColumn()) != 1) {
            return false;
        }

        if (board.isOccupied(to)) {
            return false;
        }

        List<Move> moves = history.getMoves();

        if (moves.isEmpty()) {
            return false;
        }

        Move lastMove = moves.get(moves.size() - 1);

        if (lastMove.getMoveType() != MoveType.NORMAL) {
            return false;
        }

        Piece lastPiece = lastMove.getPiece();

        if (lastPiece == null
                || lastPiece.getType() != PieceType.PAWN
                || lastPiece.getColor() == color) {
            return false;
        }

        Position lastFrom = lastMove.getFrom();
        Position lastTo = lastMove.getTo();

        int enemyStartRow = color == Color.WHITE ? 6 : 1;

        if (lastFrom.getRow() != enemyStartRow) {
            return false;
        }

        if (lastFrom.getColumn() != lastTo.getColumn()) {
            return false;
        }

        if (Math.abs(lastTo.getRow() - lastFrom.getRow()) != 2) {
            return false;
        }

        Position capturedPosition = new Position(
                from.getRow(),
                to.getColumn()
        );

        if (lastTo.getRow() != capturedPosition.getRow()
                || lastTo.getColumn() != capturedPosition.getColumn()) {
            return false;
        }

        Piece capturedPawn = board.getSquare(
                capturedPosition
        ).getPiece();

        return capturedPawn != null
                && capturedPawn.getType() == PieceType.PAWN
                && capturedPawn.getColor() != color;
    }
}
