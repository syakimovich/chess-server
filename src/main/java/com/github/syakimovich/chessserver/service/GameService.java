package com.github.syakimovich.chessserver.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.syakimovich.chessserver.consts.DrawStatus;
import com.github.syakimovich.chessserver.consts.GameStatus;
import com.github.syakimovich.chessserver.dto.GameDTO;
import com.github.syakimovich.chessserver.entities.Game;
import com.github.syakimovich.chessserver.exceptions.InvalidActionException;
import com.github.syakimovich.chessserver.repositories.GameRepository;
import com.github.syakimovich.chessserver.repositories.UserRepository;
import com.github.syakimovich.chessserver.utils.DTOConverter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GameService {

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private DTOConverter dtoConverter;

    public GameService(GameRepository gameRepository, UserRepository userRepository, DTOConverter dtoConverter) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.dtoConverter = dtoConverter;
    }

    public GameDTO findGame(long id) {
        return dtoConverter.gameToDTO(gameRepository.findByIdOrThrowException(id));
    }

    public List<GameDTO> getOpenGames() {
        return gameRepository.findByStatusIn(List.of(GameStatus.BLACK_TO_JOIN, GameStatus.WHITE_TO_JOIN))
                .stream().map(dtoConverter::gameToDTO).collect(Collectors.toList());
    }

    public List<GameDTO> getPlayerGames(String username) {
        return gameRepository.findByUsername(username).stream().map(dtoConverter::gameToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public long createGame(String creatorUsername, boolean isCreatorWhite) {
        Game game = new Game(userRepository.findByUsernameOrThrowException(creatorUsername), isCreatorWhite);
        game.setMoves(Collections.emptyList());
        if (isCreatorWhite) {
            game.setStatus(GameStatus.BLACK_TO_JOIN);
        } else {
            game.setStatus(GameStatus.WHITE_TO_JOIN);
        }
        game.setDrawStatus(DrawStatus.NO_PROPOSAL);
        return gameRepository.save(game).getId();
    }

    @Transactional(readOnly = false)
    public void join(long gameId, String opponentUsername) {
        Game game = gameRepository.findByIdOrThrowException(gameId);
        game.setOpponent(userRepository.findByUsernameOrThrowException(opponentUsername));
        game.setStatus(GameStatus.WHITE_TO_MOVE);
        gameRepository.save(game);
    }

    @Transactional(readOnly = false)
    public void proposeDraw(long gameId, String playerUsername) {
        Game game = gameRepository.findByIdOrThrowException(gameId);
        if (!DrawStatus.NO_PROPOSAL.equals(game.getDrawStatus())) {
            throw new InvalidActionException("Can't propose draw while game with id %s is in draw status: %s".formatted(game.getId(), game.getDrawStatus()));
        }
        if (game.getWhiteUser().getUsername().equals(playerUsername)) {
            game.setDrawStatus(DrawStatus.WHITE_PROPOSES_DRAW);
        } else if (game.getBlackUser().getUsername().equals(playerUsername)) {
            game.setDrawStatus(DrawStatus.BLACK_PROPOSES_DRAW);
        } else {
            throw new UsernameNotFoundException("Username %s not found".formatted(playerUsername));
        }
        gameRepository.save(game);
    }

    @Transactional(readOnly = false)
    public void acceptDraw(long gameId, String playerUsername) {
        Game game = gameRepository.findByIdOrThrowException(gameId);
        if ((DrawStatus.WHITE_PROPOSES_DRAW.equals(game.getDrawStatus()) && game.getBlackUser().getUsername().equals(playerUsername)) ||
                (DrawStatus.BLACK_PROPOSES_DRAW.equals(game.getDrawStatus()) && game.getWhiteUser().getUsername().equals(playerUsername))) {
            game.setDrawStatus(DrawStatus.DRAW_ACCEPTED);
            game.setStatus(GameStatus.DRAW);
        } else {
            throw new InvalidActionException("Player %s can't accept draw in game with id %s in draw status %s"
                    .formatted(playerUsername, game.getId(), game.getDrawStatus()));
        }

        gameRepository.save(game);
    }

    @Transactional(readOnly = false)
    public void resign(long gameId, String playerUsername) {
        Game game = gameRepository.findByIdOrThrowException(gameId);
        if (game.getBlackUser().getUsername().equals(playerUsername)) {
            game.setStatus(GameStatus.WHITE_WON);
        } else if (game.getWhiteUser().getUsername().equals(playerUsername)) {
            game.setStatus(GameStatus.BLACK_WON);
        } else {
            throw new InvalidActionException("Player %s can't resign in game with id %s"
                    .formatted(playerUsername, game.getId()));
        }

        gameRepository.save(game);
    }

    /**
     * Perform move
     * @param gameId id of the game
     * @param move move in SAN notation
     * @return true if move is valid and successfully executed, false if move is invalid
     */
    @Transactional(readOnly = false)
    public boolean move(long gameId, String move) {
        Board board = new Board();
        Game game = gameRepository.findByIdOrThrowException(gameId);
        for (String oldMove : game.getMoves()) {
            board.doMove(oldMove);
        }

        boolean isValid;
        try {
            isValid = board.doMove(move);
        } catch (MoveConversionException mce) {
            isValid = false;
        }

        if (isValid) {
            List<String> moves = game.getMoves();
            List<String> newMoves = new ArrayList<>(moves);
            newMoves.add(move);
            game.setMoves(newMoves);

            if(board.isDraw()) {
                game.setStatus(GameStatus.DRAW);
            } else if (board.isMated()) {
                if (board.getSideToMove().equals(Side.WHITE)) {
                    game.setStatus(GameStatus.BLACK_WON);
                } else {
                    game.setStatus(GameStatus.WHITE_WON);
                }
            } else {
                if (board.getSideToMove().equals(Side.WHITE)) {
                    game.setStatus(GameStatus.WHITE_TO_MOVE);
                } else {
                    game.setStatus(GameStatus.BLACK_TO_MOVE);
                }
            }

            gameRepository.save(game);
            return true;
        } else {
            return false;
        }
    }

}
