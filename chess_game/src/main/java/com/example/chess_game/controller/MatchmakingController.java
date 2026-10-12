package com.example.chess_game.controller;

import com.example.chess_game.matchmaking.MatchmakingResult;
import com.example.chess_game.matchmaking.MatchmakingService;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(
            MatchmakingService matchmakingService
    ) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/join")
    public MatchmakingResult join(JwtAuthenticationToken authentication) {

        Long userId = Long.valueOf(authentication.getName());

        return matchmakingService.joinQueue(userId);
    }

    @DeleteMapping("/leave")
    public Map<String, String> leave(
            JwtAuthenticationToken authentication
    ) {

        Long userId = Long.valueOf(authentication.getName());

        matchmakingService.leaveQueue(userId);

        return Map.of("status", "LEFT_QUEUE");
    }
}