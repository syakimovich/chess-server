package com.github.syakimovich.chessserver.controllers;

import com.github.syakimovich.chessserver.dto.GameDTO;
import com.github.syakimovich.chessserver.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
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
    }

    @GetMapping("/game/{gameId}")
    public GameDTO getGame(@PathVariable Long gameId) {
        return gameService.findGame(gameId);
    }

    @PostMapping("/game/{gameId}/move")
    public void move(@PathVariable Long gameId, @RequestBody String move) {
        boolean result = gameService.move(gameId, move);
        if (!result) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal move");
        }
    }
}
