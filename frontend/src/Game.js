import React from 'react';
import { Chess } from 'chess.js';
import { Chessboard } from "react-chessboard";
import { useState, useEffect } from "react";
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

function Game(props) {
  const [game, setGame] = useState(new Chess());
  const [gameInfo, setGameInfo] = useState({});

  useEffect(() => {
    connect();
  }, []);

  async function reloadGame() {
    const response = await fetch('/game/' + props.gameId);
    const result = await response.json();
    setGameInfo(result);
    const chessGame = new Chess();
    for(const move of result.moves) {
      chessGame.move(move);
    }
    setGame(chessGame);
  }

  function safeGameMutate(modify) {
    setGame((g) => {
      const update = { ...g };
      modify(update);
      return update;
    });
  }

  async function postMove(move) {
    await fetch('/game/' + props.gameId + '/move', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: move
    });
  }

  function onDrop(sourceSquare, targetSquare) {
    let move = null;
    safeGameMutate((game) => {
      move = game.move({
        from: sourceSquare,
        to: targetSquare,
        promotion: "q", // TODO: add promotion choice
      });
    });
    if (move === null) return false; // illegal move
    const history = game.history();
    postMove(history[history.length - 1]);
    return true;
  }

  function connect() {
    const client = Stomp.over(() => new SockJS('/ws'));
    client.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        client.subscribe('/moves/' + props.gameId, function (move) {
          console.log("message: " + move);
          reloadGame();
        });
        reloadGame();
    });
  }

  function getBoardOrientation(gameInfo) {
    if (gameInfo.creatorWhite) {
      return props.loggedInUser === gameInfo.creator ? 'white' : 'black';
    } else {
      return props.loggedInUser === gameInfo.opponent ? 'white' : 'black';
    }
  }

  function isCurrentPlayerMove(chessJsObj, gameInfo) {
    return (getBoardOrientation(gameInfo) === 'white') !== (chessJsObj.turn() !== 'w');
  }

  return <div>
    <div>Game id: {gameInfo.id}&nbsp;
    White: {gameInfo.creatorWhite ? gameInfo.creator : gameInfo.opponent}&nbsp;
    Black: {!gameInfo.creatorWhite ? gameInfo.creator : gameInfo.opponent}&nbsp; 
    Creator: {gameInfo.creator}&nbsp;
    Status: {gameInfo.status}</div>
    <div><Chessboard position={game.fen()} onPieceDrop={onDrop} boardOrientation={getBoardOrientation(gameInfo)} arePiecesDraggable={isCurrentPlayerMove(game, gameInfo)} /></div>
  </div>
}

export default Game;