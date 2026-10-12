
package com.example.chess_game.game;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.validator.AttackChecker;
import com.example.chess_game.validator.BasicMoveValidator;

public class StandardGameStateEvaluator implements GameStateEvaluator {

    private final AttackChecker attackChecker = new AttackChecker();
    private final BasicMoveValidator validator = new BasicMoveValidator();

    @Override
    public GameStatus evaluate(
            Board board,
            Color currentTurn,
            MoveHistory history
    ) {

        if (board == null || currentTurn == null || history == null) {
            throw new IllegalArgumentException("Invalid game state");
        }

        if (hasOnlyKings(board)) {
            return GameStatus.DRAW;
        }

        boolean inCheck = attackChecker.isKingInCheck(
                currentTurn,
                board
        );

        boolean hasMoves = hasLegalMoves(
                board,
                currentTurn,
                history
        );

        if (!hasMoves) {
            return inCheck
                    ? GameStatus.CHECKMATE
                    : GameStatus.STALEMATE;
        }

        return inCheck
                ? GameStatus.CHECK
                : GameStatus.IN_PROGRESS;
    }

    private boolean hasOnlyKings(Board board) {

        int pieceCount = 0;
        int kingCount = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = board.getSquare(
                        new Position(row, col)
                ).getPiece();

                if (piece != null) {
                    pieceCount++;

                    if (piece.getType() == PieceType.KING) {
                        kingCount++;
                    }
                }
            }
        }

        return pieceCount == 2 && kingCount == 2;
    }

    private boolean hasLegalMoves(
            Board board,
            Color color,
            MoveHistory history
    ) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position from = new Position(row, col);

                Piece piece = board.getSquare(from).getPiece();

                if (piece == null || piece.getColor() != color) {
                    continue;
                }

                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {

                        Position to = new Position(r, c);

                        Move move = new Move(from, to);

                        Piece target = board.getSquare(to).getPiece();

                        if (piece.getType() == PieceType.KING
                                && row == r
                                && Math.abs(c - col) == 2) {

                            move.setMoveType(MoveType.CASTLING);

                        } else if (piece.getType() == PieceType.PAWN
                                && r == (color == Color.WHITE ? 7 : 0)) {

                            move.setMoveType(MoveType.PROMOTION);
                            move.setPromotionType(PieceType.QUEEN);

                        } else if (piece.getType() == PieceType.PAWN
                                && Math.abs(c - col) == 1
                                && target == null) {

                            move.setMoveType(MoveType.EN_PASSANT);

                        } else if (target != null) {

                            move.setMoveType(MoveType.CAPTURE);
                        }

                        if (validator.isValid(
                                move,
                                board,
                                color,
                                history
                        )) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
