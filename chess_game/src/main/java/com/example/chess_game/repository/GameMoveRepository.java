package com.example.chess_game.repository;

import com.example.chess_game.entity.GameMoveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameMoveRepository extends JpaRepository<GameMoveEntity, Long> {

    List<GameMoveEntity> findByGameIdOrderByMoveNumberAsc(Long gameId);
}