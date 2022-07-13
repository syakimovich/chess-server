package com.github.syakimovich.chessserver.service;

import com.github.syakimovich.chessserver.consts.GameStatuses;
import com.github.syakimovich.chessserver.entities.Game;
import com.github.syakimovich.chessserver.repositories.GameRepository;
import com.github.syakimovich.chessserver.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Captor
    private ArgumentCaptor<Game> gameCaptor;

    private GameService gameService;

    @BeforeEach
    public void init() {
        gameService = new GameService(gameRepository, userRepository, messagingTemplate);
    }

    @Test
    void move_valid() {
        long gameId = 3;
        String move = "c4";
        Game game = new Game();
        game.setMoves(List.of("Nf3","Nf6"));
        when(gameRepository.findByIdOrThrowException(gameId)).thenReturn(game);

        boolean result = gameService.move(gameId, move);

        assertTrue(result);
        verify(gameRepository).save(gameCaptor.capture());
        Game capturedGame = gameCaptor.getValue();
        assertEquals(GameStatuses.BLACK_TO_MOVE, capturedGame.getStatus());
        assertEquals(List.of("Nf3","Nf6", move), capturedGame.getMoves());
    }

    @Test
    void move_invalid() {
        long gameId = 3;
        String move = "Nf4";
        Game game = new Game();
        game.setMoves(List.of("Nf3","Nf6"));
        when(gameRepository.findByIdOrThrowException(gameId)).thenReturn(game);

        boolean result = gameService.move(gameId, move);

        assertFalse(result);
        verify(gameRepository, times(0)).save(gameCaptor.capture());
    }

}