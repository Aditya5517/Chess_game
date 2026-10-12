
package com.example.chess_game.service;

import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.board.Board;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.model.Position;

import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class MaterialService {

    public int getPieceValue(PieceType type) {
        return switch (type) {
            case PAWN -> 1;
            case KNIGHT, BISHOP -> 3;
            case ROOK -> 5;
            case QUEEN -> 9;
            case KING -> 0;
        };
    }

    public int getMaterialScore(Board board, Color color) {
        int total = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = board.getSquare(
                        new Position(row, col)
                ).getPiece();

                if (piece != null && piece.getColor() == color) {
                    total += getPieceValue(piece.getType());
                }
            }
        }

        return total;
    }

    public int getMaterialAdvantage(Board board, Color color) {
        int whiteScore = getMaterialScore(board, Color.WHITE);
        int blackScore = getMaterialScore(board, Color.BLACK);

        if (color == Color.WHITE) {
            return whiteScore - blackScore;
        }

        return blackScore - whiteScore;
    }

    public Map<PieceType, Integer> getCapturedPieces(
            Board board,
            Color capturedBy
    ) {
        Map<PieceType, Integer> captured = new EnumMap<>(PieceType.class);

        for (PieceType type : PieceType.values()) {
            if (type != PieceType.KING) {
                captured.put(type, 0);
            }
        }

        Color opponent = capturedBy == Color.WHITE
                ? Color.BLACK
                : Color.WHITE;

        Map<PieceType, Integer> remaining = new EnumMap<>(PieceType.class);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = board.getSquare(
                        new Position(row, col)
                ).getPiece();

                if (piece != null && piece.getColor() == opponent) {
                    remaining.merge(piece.getType(), 1, Integer::sum);
                }
            }
        }

        addMissing(captured, remaining, PieceType.PAWN, 8);
        addMissing(captured, remaining, PieceType.KNIGHT, 2);
        addMissing(captured, remaining, PieceType.BISHOP, 2);
        addMissing(captured, remaining, PieceType.ROOK, 2);
        addMissing(captured, remaining, PieceType.QUEEN, 1);

        return captured;
    }

    private void addMissing(
            Map<PieceType, Integer> captured,
            Map<PieceType, Integer> remaining,
            PieceType type,
            int startingCount
    ) {
        int count = remaining.getOrDefault(type, 0);
        captured.put(type, Math.max(0, startingCount - count));
    }
}
