import React from 'react';
import { useState, useEffect } from 'react';

function Main(props) {
  const [games, setGames] = useState([]);
  useEffect(() => {
    async function fetchData() {
      const response = await fetch('/game/listopen');
      const res = await response.json();
      console.log("games:" + res);
      setGames(res);
    }
    fetchData();
  }, []);

  async function callJoinGame(gameId) {
    const response = await fetch('/game/join', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({gameId: gameId, secondPlayerUsername: props.loggedInUser})
    });
    props.openGame(gameId);
  }

  const gameList = function(games) {
    return games.map(
      (g) => (<div>{g.id} {g.creator} {g.creatorWhite + ''} <button onClick={() => callJoinGame(g.id)}>Join</button></div>)
    );
  };

  return <div>
    <div>Main</div>
    <div>{gameList(games)}</div>
    <button onClick={props.createGame}>Create new game</button>
    {JSON.stringify(games)}
  </div>
}

export default Main;