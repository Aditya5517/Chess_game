package com.example.chess_game.repository;

import com.example.chess_game.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    List<GameEntity> findByWhitePlayerIdOrBlackPlayerId(
            Long whitePlayerId,
            Long blackPlayerId
    );
}