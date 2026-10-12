package com.example.chess_game.entity;

import com.example.chess_game.enums.GameStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "white_player_id", nullable = false)
    private UserEntity whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id", nullable = false)
    private UserEntity blackPlayer;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private UserEntity winner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.NOT_STARTED;

    public GameEntity() {
    }

    public GameEntity(UserEntity whitePlayer, UserEntity blackPlayer) {

        if (whitePlayer == null || blackPlayer == null) {
            throw new IllegalArgumentException("Both players are required");
        }

        if (whitePlayer == blackPlayer ||
                (whitePlayer.getId() != null
                        && whitePlayer.getId().equals(blackPlayer.getId()))) {
            throw new IllegalArgumentException("Players must be different");
        }

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getWhitePlayer() {
        return whitePlayer;
    }

    public UserEntity getBlackPlayer() {
        return blackPlayer;
    }

    public UserEntity getWinner() {
        return winner;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setWinner(UserEntity winner) {
        this.winner = winner;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}