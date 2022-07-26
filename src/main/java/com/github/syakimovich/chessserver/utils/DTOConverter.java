package com.github.syakimovich.chessserver.utils;

import com.github.syakimovich.chessserver.dto.GameDTO;
import com.github.syakimovich.chessserver.entities.Game;
import org.springframework.stereotype.Component;

@Component
public class DTOConverter {
    public GameDTO gameToDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .creator(game.getCreator().getUsername())
                .opponent(game.getOpponent() != null ? game.getOpponent().getUsername() : null)
                .creatorWhite(game.isCreatorWhite())
                .moves(game.getMoves())
                .status(game.getStatus().toString())
                .drawStatus(game.getDrawStatus().toString()).build();
    }
}
