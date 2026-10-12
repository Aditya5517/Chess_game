package com.example.chess_game.game;

import com.example.chess_game.board.Board;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Player;

public class ChessGame {

    private Board board;

    private Player whitePlayer;
    private Player blackPlayer;
    private Color currentTurn;
    private GameStatus status;
    private MoveHistory moveHistory;
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
        if (whitePlayer.getColor() != Color.WHITE ||
                blackPlayer.getColor() != Color.BLACK) {

            throw new IllegalArgumentException(
                    "Player colors are incorrect"
            );
        }
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.status = GameStatus.NOT_STARTED;
        this.currentTurn = Color.WHITE;
        this.moveHistory = new MoveHistory();
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
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Game is not in progress"
            );
        }
        if (currentTurn == Color.WHITE) {
            currentTurn = Color.BLACK;
        } else {
            currentTurn = Color.WHITE;
        }
    }
    public void makeMove(Move move) {
        throw new UnsupportedOperationException(
                "Move execution is not implemented yet"
        );
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