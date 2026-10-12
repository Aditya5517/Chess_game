package com.example.chess_game.model;

import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.pieces.Piece;

public class Move {

    private Position from;
    private Position to;
    private Piece piece;
    private Piece capturedPiece;
    private MoveType moveType;
    private PieceType promotionType;

    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
        this.moveType = MoveType.NORMAL;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public PieceType getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(PieceType promotionType) {
        this.promotionType = promotionType;
    }
}