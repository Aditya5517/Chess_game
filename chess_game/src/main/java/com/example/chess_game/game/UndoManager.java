package com.example.chess_game.game;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.board.Board;
import com.example.chess_game.model.Move;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.model.Position;

import java.util.List;

public class UndoManager {

    private PendingUndo pendingUndo;

    public record UndoResult(
            Color restoredTurn,
            GameStatus restoredStatus
    ) {
    }

    public static class Snapshot {

        private final Piece[][] pieces;
        private final GameStatus status;

        private Snapshot(Piece[][] pieces, GameStatus status) {
            this.pieces = pieces;
            this.status = status;
        }
    }

    private record PendingUndo(
            Snapshot snapshot,
            Color movedBy,
            Move move
    ) {
    }

    public Snapshot captureSnapshot(
            Board board,
            GameStatus status
    ) {

        Piece[][] pieces = new Piece[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieces[row][col] = board.getSquare(
                        new Position(row, col)
                ).getPiece();
            }
        }

        return new Snapshot(pieces, status);
    }

    public void recordCompletedMove(
            Snapshot snapshot,
            Color movedBy,
            Move move
    ) {

        if (snapshot == null || movedBy == null || move == null) {
            throw new IllegalArgumentException(
                    "Invalid undo information"
            );
        }

        pendingUndo = new PendingUndo(snapshot, movedBy, move);
    }

    public UndoResult undo(
            Board board,
            MoveHistory history,
            Color requestedBy,
            Color currentTurn,
            GameStatus currentStatus
    ) {

        if (pendingUndo == null) {
            throw new IllegalStateException("No move to undo");
        }

        if (currentStatus != GameStatus.IN_PROGRESS
                && currentStatus != GameStatus.CHECK) {
            throw new IllegalStateException(
                    "Cannot undo a finished game"
            );
        }

        if (requestedBy != pendingUndo.movedBy()) {
            throw new IllegalStateException(
                    "You can only undo your own move"
            );
        }

        if (currentTurn == requestedBy) {
            throw new IllegalStateException(
                    "Undo is only available before the opponent responds"
            );
        }

        List<Move> moves = history.getMoves();

        if (moves.isEmpty()
                || moves.get(moves.size() - 1) != pendingUndo.move()) {
            throw new IllegalStateException(
                    "The last move has changed"
            );
        }

        Snapshot snapshot = pendingUndo.snapshot();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.getSquare(
                        new Position(row, col)
                ).setPiece(snapshot.pieces[row][col]);
            }
        }

        history.undo();

        UndoResult result = new UndoResult(
                pendingUndo.movedBy(),
                snapshot.status
        );

        pendingUndo = null;

        return result;
    }
}
