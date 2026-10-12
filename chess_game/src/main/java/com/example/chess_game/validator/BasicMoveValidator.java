package com.example.chess_game.validator;

import com.example.chess_game.board.Board;
import com.example.chess_game.board.Square;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.rule.CastlingRule;
import com.example.chess_game.rule.EnPassantRule;
import com.example.chess_game.rule.PromotionRule;

public class BasicMoveValidator implements MoveValidator {

    private final CastlingRule castlingRule = new CastlingRule();
    private final EnPassantRule enPassantRule = new EnPassantRule();
    private final PromotionRule promotionRule = new PromotionRule();
    private final AttackChecker attackChecker = new AttackChecker();

    @Override
    public boolean isValid(
            Move move,
            Board board,
            Color currentTurn,
            MoveHistory history
    ) {

        if (move == null || board == null
                || currentTurn == null || history == null) {
            return false;
        }

        Position from = move.getFrom();
        Position to = move.getTo();

        if (from == null || to == null || move.getMoveType() == null) {
            return false;
        }

        if (from.getRow() == to.getRow()
                && from.getColumn() == to.getColumn()) {
            return false;
        }

        Piece piece = board.getSquare(from).getPiece();
        Piece destination = board.getSquare(to).getPiece();

        if (piece == null || piece.getColor() != currentTurn) {
            return false;
        }

        if (move.getPiece() != null && move.getPiece() != piece) {
            return false;
        }

        if (destination != null) {

            if (destination.getColor() == currentTurn
                    || destination.getType() == PieceType.KING) {
                return false;
            }
        }

        MoveType type = move.getMoveType();

        boolean valid;

        switch (type) {

            case NORMAL:
                valid = destination == null
                        && piece.isValidMove(from, to, board);

                if (piece.getType() == PieceType.PAWN) {
                    int lastRow = currentTurn == Color.WHITE ? 7 : 0;

                    if (to.getRow() == lastRow) {
                        valid = false;
                    }
                }

                break;

            case CAPTURE:
                valid = destination != null
                        && piece.isValidMove(from, to, board);

                if (piece.getType() == PieceType.PAWN) {
                    int lastRow = currentTurn == Color.WHITE ? 7 : 0;

                    if (to.getRow() == lastRow) {
                        valid = false;
                    }
                }

                break;

            case CASTLING:
                valid = castlingRule.isValid(move, board, history);
                break;

            case EN_PASSANT:
                valid = enPassantRule.isValid(move, board, history);
                break;

            case PROMOTION:
                valid = promotionRule.isValid(move, board, history);
                break;

            default:
                valid = false;
        }

        if (!valid) {
            return false;
        }

        return isKingSafeAfterMove(
                move, board, currentTurn
        );
    }

    private boolean isKingSafeAfterMove(
            Move move,
            Board board,
            Color color
    ) {

        Position from = move.getFrom();
        Position to = move.getTo();

        Square source = board.getSquare(from);
        Square target = board.getSquare(to);

        Piece movingPiece = source.getPiece();
        Piece capturedPiece = target.getPiece();

        Square extraSquare = null;
        Piece extraPiece = null;

        Square rookSource = null;
        Square rookTarget = null;
        Piece rook = null;
        Piece rookTargetPiece = null;

        if (move.getMoveType() == MoveType.EN_PASSANT) {

            Position capturedPosition = new Position(
                    from.getRow(),
                    to.getColumn()
            );

            extraSquare = board.getSquare(capturedPosition);
            extraPiece = extraSquare.getPiece();

            extraSquare.setPiece(null);
        }

        if (move.getMoveType() == MoveType.CASTLING) {

            int row = from.getRow();
            boolean shortCastle = to.getColumn() == 6;

            int rookFromColumn = shortCastle ? 7 : 0;
            int rookToColumn = shortCastle ? 5 : 3;

            rookSource = board.getSquare(
                    new Position(row, rookFromColumn)
            );

            rookTarget = board.getSquare(
                    new Position(row, rookToColumn)
            );

            rook = rookSource.getPiece();
            rookTargetPiece = rookTarget.getPiece();

            rookSource.setPiece(null);
            rookTarget.setPiece(rook);
        }

        source.setPiece(null);
        target.setPiece(movingPiece);

        boolean safe;

        try {
            safe = !attackChecker.isKingInCheck(color, board);
        } finally {

            source.setPiece(movingPiece);
            target.setPiece(capturedPiece);

            if (extraSquare != null) {
                extraSquare.setPiece(extraPiece);
            }

            if (rookSource != null) {
                rookSource.setPiece(rook);
                rookTarget.setPiece(rookTargetPiece);
            }
        }

        return safe;
    }
}