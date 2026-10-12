package com.example.chess_game.service;

import com.example.chess_game.entity.GameEntity;
import com.example.chess_game.entity.UserEntity;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.repository.GameRepository;
import com.example.chess_game.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EloService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public EloService(
            UserRepository userRepository,
            GameRepository gameRepository
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    private int calculateRating(
            int myRating,
            int opponentRating,
            double score
    ) {

        double expected = 1.0 /
                (1.0 + Math.pow(
                        10.0,
                        (opponentRating - myRating) / 400.0
                ));

        return Math.max(100, (int) Math.round(
                myRating + 32 * (score - expected)
        ));
    }

    @Transactional
    public void updateRatings(
            Long gameId,
            Long winnerId,
            boolean draw
    ) {

        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Game not found"
                ));

        if (game.getStatus() != GameStatus.IN_PROGRESS
                && game.getStatus() != GameStatus.CHECK) {
            throw new IllegalStateException(
                    "Game has already finished"
            );
        }

        UserEntity white = game.getWhitePlayer();
        UserEntity black = game.getBlackPlayer();

        double whiteScore;

        if (draw) {

            if (winnerId != null) {
                throw new IllegalArgumentException(
                        "Draw cannot have a winner"
                );
            }

            whiteScore = 0.5;

        } else if (winnerId != null
                && winnerId.equals(white.getId())) {

            whiteScore = 1.0;

        } else if (winnerId != null
                && winnerId.equals(black.getId())) {

            whiteScore = 0.0;

        } else {

            throw new IllegalArgumentException("Invalid winner");
        }

        int whiteRating = white.getEloRating();
        int blackRating = black.getEloRating();

        int newWhiteRating = calculateRating(
                whiteRating,
                blackRating,
                whiteScore
        );

        int newBlackRating = calculateRating(
                blackRating,
                whiteRating,
                1.0 - whiteScore
        );

        white.setEloRating(newWhiteRating);
        black.setEloRating(newBlackRating);

        game.setWinner(
                draw ? null
                        : whiteScore == 1.0 ? white : black
        );

        game.setStatus(
                draw ? GameStatus.DRAW : GameStatus.CHECKMATE
        );

        userRepository.save(white);
        userRepository.save(black);
        gameRepository.save(game);
    }
}