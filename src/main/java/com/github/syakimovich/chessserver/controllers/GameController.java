package com.github.syakimovich.chessserver.controllers;

import com.github.syakimovich.chessserver.dto.GameDTO;
import com.github.syakimovich.chessserver.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {
    private GameService gameService;
    private SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/game/create")
    public Long createNewGame(@RequestBody GameDTO newGame) {
        return gameService.createGame(newGame.getCreator(), newGame.isCreatorWhite());
    }

    @GetMapping("/game/listopen")
    public List<GameDTO> listOpenGames() {
        return gameService.getOpenGames();
    }

    @PostMapping("/game/join")
    public void join(@RequestBody GameDTO dto) {
        gameService.join(dto.getId(), dto.getOpponent());
        messagingTemplate.convertAndSend("/game/" + dto.getId(), "");
    }

    @PostMapping("/game/{gameId}/proposeDraw")
    public void proposeDraw(@PathVariable Long gameId, @RequestBody String username) {
        gameService.proposeDraw(gameId, username);
        messagingTemplate.convertAndSend("/game/" + gameId, "");
    }

    @PostMapping("/game/{gameId}/acceptDraw")
    public void acceptDraw(@PathVariable Long gameId, @RequestBody String username) {
        gameService.acceptDraw(gameId, username);
        messagingTemplate.convertAndSend("/game/" + gameId, "");
    }

    @GetMapping("/game/{gameId}")
    public GameDTO getGame(@PathVariable Long gameId) {
        return gameService.findGame(gameId);
    }

    @PostMapping("/game/{gameId}/move")
    public void move(@PathVariable Long gameId, @RequestBody String move) {
        boolean result = gameService.move(gameId, move);
        if (result) {
            messagingTemplate.convertAndSend("/game/" + gameId, move);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal move");
        }
    }
}
