package com.example.chess_game.controller;

import com.example.chess_game.dto.GameResponse;
import com.example.chess_game.entity.GameEntity;
import com.example.chess_game.entity.GameMoveEntity;
import com.example.chess_game.repository.GameMoveRepository;
import com.example.chess_game.repository.GameRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository gameRepository;
    private final GameMoveRepository gameMoveRepository;

    public GameController(
            GameRepository gameRepository,
            GameMoveRepository gameMoveRepository
    ) {
        this.gameRepository = gameRepository;
        this.gameMoveRepository = gameMoveRepository;
    }

    @GetMapping
    public List<GameResponse> getMyGames(
            JwtAuthenticationToken authentication
    ) {

        Long userId = Long.valueOf(authentication.getName());

        return gameRepository.findByWhitePlayerIdOrBlackPlayerId(
                        userId,
                        userId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public GameResponse getGame(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {

        GameEntity game = findAuthorizedGame(
                id,
                Long.valueOf(authentication.getName())
        );

        return toResponse(game);
    }

    @GetMapping("/{id}/moves")
    public List<MoveResponse> getMoves(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {

        findAuthorizedGame(
                id,
                Long.valueOf(authentication.getName())
        );

        return gameMoveRepository
                .findByGameIdOrderByMoveNumberAsc(id)
                .stream()
                .map(move -> new MoveResponse(
                        move.getMoveNumber(),
                        move.getFromSquare(),
                        move.getToSquare(),
                        move.getMoveType().name()
                ))
                .toList();
    }

    private GameEntity findAuthorizedGame(
            Long gameId,
            Long userId
    ) {

        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Game not found"
                ));

        boolean whitePlayer = game.getWhitePlayer()
                .getId().equals(userId);

        boolean blackPlayer = game.getBlackPlayer()
                .getId().equals(userId);

        if (!whitePlayer && !blackPlayer) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        return game;
    }

    private GameResponse toResponse(GameEntity game) {

        String winner = game.getWinner() == null
                ? null
                : game.getWinner().getUsername();

        return new GameResponse(
                game.getId(),
                game.getWhitePlayer().getUsername(),
                game.getBlackPlayer().getUsername(),
                game.getStatus(),
                winner
        );
    }

    public record MoveResponse(
            int moveNumber,
            String from,
            String to,
            String type
    ) {
    }
}