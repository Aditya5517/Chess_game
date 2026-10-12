
package com.example.chess_game.game;

import com.example.chess_game.board.Board;
import com.example.chess_game.board.Square;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Player;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.pieces.PieceFactory;
import com.example.chess_game.validator.AttackChecker;
import com.example.chess_game.validator.BasicMoveValidator;
import com.example.chess_game.validator.MoveValidator;

public class ChessGame {

    private Board board;

    private final Player whitePlayer;
    private final Player blackPlayer;
    private Color currentTurn;
    private GameStatus status;
    private final MoveHistory moveHistory;

    private final MoveValidator moveValidator;
    private final AttackChecker attackChecker;

    public ChessGame(Player whitePlayer, Player blackPlayer) {

        if (whitePlayer == null || blackPlayer == null) {
            throw new IllegalArgumentException(
                    "Both players are required"
            );
        }

        if (whitePlayer == blackPlayer) {
            throw new IllegalArgumentException(
                    "A player cannot play against themselves"
            );
        }

        if (whitePlayer.getColor() != Color.WHITE
                || blackPlayer.getColor() != Color.BLACK) {
            throw new IllegalArgumentException(
                    "Player colors are incorrect"
            );
        }

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentTurn = Color.WHITE;
        this.status = GameStatus.NOT_STARTED;
        this.moveHistory = new MoveHistory();

        this.moveValidator = new BasicMoveValidator();
        this.attackChecker = new AttackChecker();
    }

    public void start() {

        if (status != GameStatus.NOT_STARTED) {
            throw new IllegalStateException(
                    "Game has already started"
            );
        }

        board = new Board();
        currentTurn = Color.WHITE;
        status = GameStatus.IN_PROGRESS;
    }

    public void switchTurn() {

        if (status != GameStatus.IN_PROGRESS
                && status != GameStatus.CHECK) {
            throw new IllegalStateException(
                    "Game is not active"
            );
        }

        currentTurn = currentTurn == Color.WHITE
                ? Color.BLACK
                : Color.WHITE;
    }

    public synchronized void makeMove(Move move) {

        if (status != GameStatus.IN_PROGRESS
                && status != GameStatus.CHECK) {
            throw new IllegalStateException(
                    "Game is not active"
            );
        }

        if (!moveValidator.isValid(
                move,
                board,
                currentTurn,
                moveHistory
        )) {
            throw new IllegalArgumentException(
                    "Invalid chess move"
            );
        }

        Position from = move.getFrom();
        Position to = move.getTo();

        Square source = board.getSquare(from);
        Square destination = board.getSquare(to);

        Piece movingPiece = source.getPiece();
        Piece capturedPiece = destination.getPiece();

        MoveType type = move.getMoveType();

        move.setPiece(movingPiece);

        if (type == MoveType.EN_PASSANT) {

            Position capturedPosition = new Position(
                    from.getRow(),
                    to.getColumn()
            );

            Square capturedSquare =
                    board.getSquare(capturedPosition);

            capturedPiece = capturedSquare.getPiece();
            capturedSquare.setPiece(null);
        }

        if (type == MoveType.CASTLING) {

            int row = from.getRow();

            boolean kingSide = to.getColumn() == 6;

            int rookFromColumn = kingSide ? 7 : 0;
            int rookToColumn = kingSide ? 5 : 3;

            Square rookSource = board.getSquare(
                    new Position(row, rookFromColumn)
            );

            Square rookDestination = board.getSquare(
                    new Position(row, rookToColumn)
            );

            Piece rook = rookSource.getPiece();

            rookSource.setPiece(null);
            rookDestination.setPiece(rook);
        }

        source.setPiece(null);

        if (type == MoveType.PROMOTION) {

            Piece promotedPiece = PieceFactory.createPiece(
                    move.getPromotionType(),
                    currentTurn
            );

            destination.setPiece(promotedPiece);

        } else {

            destination.setPiece(movingPiece);
        }

        move.setCapturedPiece(capturedPiece);

        moveHistory.add(move);

        switchTurn();

        status = attackChecker.isKingInCheck(
                currentTurn,
                board
        ) ? GameStatus.CHECK : GameStatus.IN_PROGRESS;
    }

    public boolean isGameOver() {

        return status == GameStatus.CHECKMATE
                || status == GameStatus.STALEMATE
                || status == GameStatus.DRAW;
    }

    public Board getBoard() {
        return board;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public MoveHistory getMoveHistory() {
        return moveHistory;
    }
}
