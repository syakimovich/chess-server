package com.github.syakimovich.chessserver.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.syakimovich.chessserver.consts.GameStatuses;
import com.github.syakimovich.chessserver.dto.GameDTO;
import com.github.syakimovich.chessserver.entities.Game;
import com.github.syakimovich.chessserver.repositories.GameRepository;
import com.github.syakimovich.chessserver.repositories.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;

    public GameService(GameRepository gameRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public GameDTO findGame(long id) {
        return gameToDTO(gameRepository.findByIdOrThrowException(id));
    }

    public List<GameDTO> getOpenGames() {
        return gameRepository.findByStatusIn(List.of(GameStatuses.BLACK_TO_JOIN, GameStatuses.WHITE_TO_JOIN))
                .stream().map(this::gameToDTO).collect(Collectors.toList());
    }

    public long createGame(String creatorUsername, boolean isCreatorWhite) {
        Game game = new Game(userRepository.findByUsernameOrThrowException(creatorUsername), isCreatorWhite);
        game.setMoves(Collections.emptyList());
        if (isCreatorWhite) {
            game.setStatus(GameStatuses.BLACK_TO_JOIN);
        } else {
            game.setStatus(GameStatuses.WHITE_TO_JOIN);
        }
        return gameRepository.save(game).getId();
    }

    public void join(long gameId, String opponentUsername) {
        Game game = gameRepository.findByIdOrThrowException(gameId);
        game.setOpponent(userRepository.findByUsernameOrThrowException(opponentUsername));
        game.setStatus(GameStatuses.WHITE_TO_MOVE);
        gameRepository.save(game);
        messagingTemplate.convertAndSend("/moves/" + gameId, "");
    }

    /**
     * Perform move
     * @param gameId
     * @param move
     * @return true if move is valid and successfully executed, false if move is invalid
     */
    public boolean move(long gameId, String move) {
        Board board = new Board();
        Game game = gameRepository.findByIdOrThrowException(gameId);
        for (String oldMove : game.getMoves()) {
            board.doMove(oldMove);
        }
        boolean isValid = board.doMove(move);
        if (isValid) {
            List<String> moves = game.getMoves();
            List<String> newMoves = new ArrayList<>(moves);
            newMoves.add(move);
            game.setMoves(newMoves);

            if(board.isDraw()) {
                game.setStatus(GameStatuses.DRAW);
            } else if (board.isMated()) {
                if (board.getSideToMove().equals(Side.WHITE)) {
                    game.setStatus(GameStatuses.BLACK_WON);
                } else {
                    game.setStatus(GameStatuses.WHITE_WON);
                }
            } else {
                if (board.getSideToMove().equals(Side.WHITE)) {
                    game.setStatus(GameStatuses.WHITE_TO_MOVE);
                } else {
                    game.setStatus(GameStatuses.BLACK_TO_MOVE);
                }
            }

            gameRepository.save(game);
            messagingTemplate.convertAndSend("/moves/" + gameId, move);
            return true;
        } else {
            return false;
        }
    }

    public GameDTO gameToDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .creator(game.getCreator().getUsername())
                .opponent(game.getOpponent() != null ? game.getOpponent().getUsername() : null)
                .creatorWhite(game.isCreatorWhite())
                .moves(game.getMoves())
                .status(game.getStatus()).build();
    }
}
