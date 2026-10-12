
package com.example.chess_game.service;

import com.example.chess_game.board.Board;
import com.example.chess_game.entity.GameEntity;
import com.example.chess_game.entity.GameMoveEntity;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.game.ChessGame;
import com.example.chess_game.game.StandardGameStateEvaluator;
import com.example.chess_game.model.Move;
import com.example.chess_game.model.Player;
import com.example.chess_game.model.Position;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.repository.GameMoveRepository;
import com.example.chess_game.repository.GameRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameSessionService {

    private final GameRepository gameRepository;
    private final GameMoveRepository gameMoveRepository;
    private final StandardGameStateEvaluator evaluator =
            new StandardGameStateEvaluator();

    public GameSessionService(
            GameRepository gameRepository,
            GameMoveRepository gameMoveRepository
    ) {
        this.gameRepository = gameRepository;
        this.gameMoveRepository = gameMoveRepository;
    }

    @Transactional(readOnly = true)
    public BoardState getState(Long gameId, Long userId) {

        GameEntity entity = findAuthorizedGame(gameId, userId);
        ChessGame game = rebuildGame(entity);

        return toBoardState(entity, game);
    }

    @Transactional
    public synchronized BoardState makeMove(
            Long gameId,
            Long userId,
            String fromSquare,
            String toSquare,
            PieceType promotionType
    ) {

        GameEntity entity = findAuthorizedGame(gameId, userId);

        if (entity.getStatus() != GameStatus.IN_PROGRESS
                && entity.getStatus() != GameStatus.CHECK) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Game is already finished"
            );
        }

        ChessGame game = rebuildGame(entity);

        boolean isWhite = entity.getWhitePlayer()
                .getId().equals(userId);

        Color playerColor = isWhite
                ? Color.WHITE
                : Color.BLACK;

        if (game.getCurrentTurn() != playerColor) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "It is not your turn"
            );
        }

        Position from = parsePosition(fromSquare);
        Position to = parsePosition(toSquare);

        Piece movingPiece = game.getBoard()
                .getSquare(from).getPiece();

        if (movingPiece == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "There is no piece on the selected square"
            );
        }

        Move move = new Move(from, to);

        Piece target = game.getBoard()
                .getSquare(to).getPiece();

        MoveType type = MoveType.NORMAL;

        if (movingPiece.getType() == PieceType.KING
                && from.getRow() == to.getRow()
                && Math.abs(to.getColumn() - from.getColumn()) == 2) {

            type = MoveType.CASTLING;

        } else if (movingPiece.getType() == PieceType.PAWN
                && to.getRow() == (playerColor == Color.WHITE ? 7 : 0)) {

            type = MoveType.PROMOTION;

            move.setPromotionType(
                    promotionType == null ? PieceType.QUEEN : promotionType
            );

        } else if (movingPiece.getType() == PieceType.PAWN
                && from.getColumn() != to.getColumn()
                && target == null) {

            type = MoveType.EN_PASSANT;

        } else if (target != null) {

            type = MoveType.CAPTURE;
        }

        move.setMoveType(type);

        try {
            game.makeMove(move);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage()
            );
        }

        int moveNumber = game.getMoveHistory()
                .getMoves().size();

        GameMoveEntity savedMove = new GameMoveEntity(
                entity,
                moveNumber,
                formatPosition(from),
                formatPosition(to),
                move.getMoveType(),
                move.getPromotionType()
        );

        gameMoveRepository.save(savedMove);

        GameStatus newStatus = evaluator.evaluate(
                game.getBoard(),
                game.getCurrentTurn(),
                game.getMoveHistory()
        );

        entity.setStatus(newStatus);

        if (newStatus == GameStatus.CHECKMATE) {

            if (game.getCurrentTurn() == Color.WHITE) {
                entity.setWinner(entity.getBlackPlayer());
            } else {
                entity.setWinner(entity.getWhitePlayer());
            }
        }

        gameRepository.save(entity);

        return toBoardState(entity, game);
    }

    private ChessGame rebuildGame(GameEntity entity) {

        Player white = new Player(
                entity.getWhitePlayer().getUsername(),
                Color.WHITE,
                entity.getWhitePlayer().getEloRating()
        );

        Player black = new Player(
                entity.getBlackPlayer().getUsername(),
                Color.BLACK,
                entity.getBlackPlayer().getEloRating()
        );

        ChessGame game = new ChessGame(white, black);
        game.start();

        List<GameMoveEntity> savedMoves = gameMoveRepository
                .findByGameIdOrderByMoveNumberAsc(entity.getId());

        for (GameMoveEntity savedMove : savedMoves) {

            Move move = new Move(
                    parsePosition(savedMove.getFromSquare()),
                    parsePosition(savedMove.getToSquare())
            );

            move.setMoveType(savedMove.getMoveType());
            move.setPromotionType(savedMove.getPromotionType());

            game.makeMove(move);
        }

        return game;
    }

    private GameEntity findAuthorizedGame(
            Long gameId,
            Long userId
    ) {

        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Game not found"
                ));

        boolean white = entity.getWhitePlayer()
                .getId().equals(userId);

        boolean black = entity.getBlackPlayer()
                .getId().equals(userId);

        if (!white && !black) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        return entity;
    }

    private Position parsePosition(String square) {

        if (square == null
                || !square.toUpperCase().matches("[A-H][1-8]")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid square"
            );
        }

        String value = square.toUpperCase();

        int column = value.charAt(0) - 'A';
        int row = value.charAt(1) - '1';

        return new Position(row, column);
    }

    private String formatPosition(Position position) {

        char file = (char) ('A' + position.getColumn());
        int rank = position.getRow() + 1;

        return "" + file + rank;
    }

    private BoardState toBoardState(
            GameEntity entity,
            ChessGame game
    ) {

        List<String> rows = new ArrayList<>();

        Board board = game.getBoard();

        for (int row = 7; row >= 0; row--) {

            StringBuilder line = new StringBuilder();

            for (int col = 0; col < 8; col++) {

                Piece piece = board.getSquare(
                        new Position(row, col)
                ).getPiece();

                if (piece == null) {
                    line.append(".");
                    continue;
                }

                char symbol = switch (piece.getType()) {
                    case KING -> 'K';
                    case QUEEN -> 'Q';
                    case ROOK -> 'R';
                    case BISHOP -> 'B';
                    case KNIGHT -> 'N';
                    case PAWN -> 'P';
                };

                if (piece.getColor() == Color.BLACK) {
                    symbol = Character.toLowerCase(symbol);
                }

                line.append(symbol);
            }

            rows.add(line.toString());
        }

        return new BoardState(
                entity.getId(),
                rows,
                game.getCurrentTurn(),
                entity.getStatus(),
                entity.getWinner() == null
                        ? null
                        : entity.getWinner().getUsername(),
                game.getMoveHistory().getMoves().size()
        );
    }

    public record BoardState(
            Long gameId,
            List<String> board,
            Color currentTurn,
            GameStatus status,
            String winner,
            int moveCount
    ) {
    }
}
