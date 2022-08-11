package com.github.syakimovich.chessserver.repositories;

import com.github.syakimovich.chessserver.consts.GameStatus;
import com.github.syakimovich.chessserver.entities.Game;
import com.github.syakimovich.chessserver.exceptions.GameNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    default Game findByIdOrThrowException(long id) {
        return findById(id).orElseThrow(() -> new GameNotFoundException("Game with id %s not found".formatted(id)));
    }

    List<Game> findByStatusIn(Collection<GameStatus> statuses);

    @Query("select g from Game g where g.creator.username = ?1 or g.opponent.username =?1")
    List<Game> findByUsername(String username);
}
