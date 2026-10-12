
package com.example.chess_game.service;

import com.example.chess_game.dto.LegalMoveResponse;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.MoveHistory;
import com.example.chess_game.board.Board;
import com.example.chess_game.model.Move;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.model.Position;
import com.example.chess_game.validator.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class LegalMoveService {

    private final MoveValidator moveValidator;

    public LegalMoveService(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    public List<LegalMoveResponse> getLegalMoves(
            Board board,
            Position from,
            Color currentTurn,
            MoveHistory history
    ) {

        List<LegalMoveResponse> legalMoves = new ArrayList<>();

        if (board == null || from == null
                || currentTurn == null || history == null) {
            return legalMoves;
        }

        Piece piece = board.getSquare(from).getPiece();

        if (piece == null || piece.getColor() != currentTurn) {
            return legalMoves;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position to = new Position(row, col);

                if (from.getRow() == row && from.getColumn() == col) {
                    continue;
                }

                MoveType type = findMoveType(board, from, to, piece);

                Move move = new Move(from, to);
                move.setMoveType(type);

                if (type == MoveType.PROMOTION) {
                    move.setPromotionType(PieceType.QUEEN);
                }

                if (moveValidator.isValid(
                        move,
                        board,
                        currentTurn,
                        history
                )) {

                    boolean capture =
                            type == MoveType.CAPTURE
                                    || type == MoveType.EN_PASSANT
                                    || (
                                    type == MoveType.PROMOTION
                                            && board.getSquare(to).getPiece() != null
                            );

                    legalMoves.add(
                            new LegalMoveResponse(
                                    toNotation(to),
                                    type,
                                    capture
                            )
                    );
                }
            }
        }

        return legalMoves;
    }

    private MoveType findMoveType(
            Board board,
            Position from,
            Position to,
            Piece piece
    ) {

        int rowDifference = to.getRow() - from.getRow();
        int colDifference = to.getColumn() - from.getColumn();

        Piece target = board.getSquare(to).getPiece();

        if (piece.getType() == PieceType.KING
                && rowDifference == 0
                && Math.abs(colDifference) == 2) {

            return MoveType.CASTLING;
        }

        if (piece.getType() == PieceType.PAWN) {

            int promotionRow =
                    piece.getColor() == Color.WHITE ? 7 : 0;

            if (to.getRow() == promotionRow) {
                return MoveType.PROMOTION;
            }

            if (Math.abs(colDifference) == 1 && target == null) {
                return MoveType.EN_PASSANT;
            }
        }

        if (target != null) {
            return MoveType.CAPTURE;
        }

        return MoveType.NORMAL;
    }

    private String toNotation(Position position) {

        char file = (char) ('A' + position.getColumn());

        int rank = position.getRow() + 1;

        return "" + file + rank;
    }
}
