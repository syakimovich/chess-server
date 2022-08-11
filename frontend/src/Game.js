import React from 'react';
import { Chess } from 'chess.js';
import { Chessboard } from "react-chessboard";
import { useState, useEffect } from "react";
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import Button from 'react-bootstrap/Button';

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

  function postMove(move) {
    fetch('/game/' + props.gameId + '/move', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: move
    });
  }

  function proposeDraw() {
    fetch('/game/' + props.gameId + '/proposeDraw', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: props.loggedInUser
    });
  }

  function acceptDraw() {
    fetch('/game/' + props.gameId + '/acceptDraw', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: props.loggedInUser
    });
  }

  function resign() {
    fetch('/game/' + props.gameId + '/resign', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: props.loggedInUser
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
        client.subscribe('/game/' + props.gameId, function (move) {
          console.log("message: " + move);
          reloadGame();
        });
        reloadGame();
    });
  }

  function getBoardOrientation(gameParam) {
    if (gameParam.creatorWhite) {
      return props.loggedInUser === gameParam.creator ? 'white' : 'black';
    } else {
      return props.loggedInUser === gameParam.opponent ? 'white' : 'black';
    }
  }

  function isCurrentPlayerMove(gameParam) {
    return (gameParam.status === 'WHITE_TO_MOVE' && getBoardOrientation(gameParam) === 'white') || 
      (gameParam.status === 'BLACK_TO_MOVE' && getBoardOrientation(gameParam) === 'black');
  }

  function getWhiteUsername(gameParam) {
    return gameParam.creatorWhite ? gameParam.creator : gameParam.opponent;
  }

  function getBlackUsername(gameParam) {
    return !gameParam.creatorWhite ? gameParam.creator : gameParam.opponent;
  }

  function getDrawButton(gameParam) {
    if (gameParam.drawStatus === 'NO_PROPOSAL') {
      return <Button onClick={proposeDraw} >Propose draw</Button>
    }
    if (gameParam.drawStatus === 'WHITE_PROPOSES_DRAW') {
      if (props.loggedInUser === getBlackUsername(gameParam)) {
        return <Button onClick={acceptDraw} >Accept draw</Button>
      }
    }
    if (gameParam.drawStatus === 'BLACK_PROPOSES_DRAW') {
      if (props.loggedInUser === getWhiteUsername(gameParam)) {
        return <Button onClick={acceptDraw} >Accept draw</Button>
      }
    }
  }

  function getResignButton(gameParam) {
    if (gameParam.status === 'WHITE_TO_MOVE' || gameParam.status === 'BLACK_TO_MOVE') {
      return <Button onClick={resign} >Resign</Button>
    }
  }

  return <div>
    <div>Game id: {gameInfo.id}&nbsp;
    White: {getWhiteUsername(gameInfo)}&nbsp;
    Black: {getBlackUsername(gameInfo)}&nbsp;
    Creator: {gameInfo.creator}&nbsp;
    Status: {gameInfo.status}&nbsp;
    Draw status: {gameInfo.drawStatus}</div>
    <div>
      {getDrawButton(gameInfo)}
      {getResignButton(gameInfo)}
    </div>
    <div><Chessboard position={game.fen()} onPieceDrop={onDrop} boardOrientation={getBoardOrientation(gameInfo)} arePiecesDraggable={isCurrentPlayerMove(gameInfo)} /></div>
  </div>
}

export default Game;