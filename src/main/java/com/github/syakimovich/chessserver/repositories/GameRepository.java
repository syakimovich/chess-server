package com.github.syakimovich.chessserver.repositories;

import com.github.syakimovich.chessserver.consts.GameStatus;
import com.github.syakimovich.chessserver.entities.Game;
import com.github.syakimovich.chessserver.exceptions.GameNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    default Game findByIdOrThrowException(long id) {
        return findById(id).orElseThrow(() -> new GameNotFoundException("Game with id %s not found".formatted(id)));
    }

    List<Game> findByStatusIn(Collection<GameStatus> statuses);
}
