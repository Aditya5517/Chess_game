package com.example.chess_game.matchmaking;

import com.example.chess_game.entity.GameEntity;
import com.example.chess_game.entity.UserEntity;
import com.example.chess_game.enums.GameStatus;
import com.example.chess_game.repository.GameRepository;
import com.example.chess_game.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

@Service
public class MatchmakingService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Integer, Queue<Long>> queues = new HashMap<>();
    private final Set<Long> waitingPlayers = new HashSet<>();

    public MatchmakingService(
            UserRepository userRepository,
            GameRepository gameRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;

        for (int i = 1; i <= 5; i++) {
            queues.put(i, new ArrayDeque<>());
        }
    }

    private int getQueueNumber(int elo) {

        if (elo <= 800) return 1;
        if (elo <= 1200) return 2;
        if (elo <= 1600) return 3;
        if (elo <= 2000) return 4;

        return 5;
    }

    public synchronized MatchmakingResult joinQueue(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"
                ));

        if (user.getEloRating() < 100) {
            throw new IllegalStateException("Invalid Elo rating");
        }

        if (waitingPlayers.contains(userId)) {
            throw new IllegalStateException("Already in matchmaking");
        }

        if (hasActiveGame(userId)) {
            throw new IllegalStateException("Player already has an active game");
        }

        int queueNumber = getQueueNumber(user.getEloRating());

        Queue<Long> queue = queues.get(queueNumber);

        if (queue.isEmpty()) {

            queue.offer(userId);
            waitingPlayers.add(userId);

            return new MatchmakingResult("WAITING", null);
        }

        Long opponentId = queue.peek();

        UserEntity opponent = userRepository.findById(opponentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Waiting opponent not found"
                ));

        if (hasActiveGame(opponentId)) {
            queue.poll();
            waitingPlayers.remove(opponentId);
            return joinQueue(userId);
        }

        GameEntity game = new GameEntity(opponent, user);
        game.setStatus(GameStatus.IN_PROGRESS);

        GameEntity savedGame = gameRepository.save(game);

        queue.poll();
        waitingPlayers.remove(opponentId);

        MatchmakingResult result = new MatchmakingResult(
                "MATCHED",
                savedGame.getId()
        );

        messagingTemplate.convertAndSendToUser(
                opponentId.toString(),
                "/queue/notifications",
                result
        );

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                result
        );

        return result;
    }

    public synchronized void leaveQueue(Long userId) {

        for (Queue<Long> queue : queues.values()) {
            queue.remove(userId);
        }

        waitingPlayers.remove(userId);
    }

    private boolean hasActiveGame(Long userId) {

        List<GameEntity> games =
                gameRepository.findByWhitePlayerIdOrBlackPlayerId(
                        userId, userId
                );

        for (GameEntity game : games) {

            if (game.getStatus() == GameStatus.IN_PROGRESS
                    || game.getStatus() == GameStatus.CHECK) {
                return true;
            }
        }

        return false;
    }
}