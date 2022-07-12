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
      body: JSON.stringify({gameId: gameId, opponent: props.loggedInUser})
    });
    props.openGame(gameId);
  }

  const gameList = function(games) {
    return games.map(
      (g) => (<tr>
          <td>{g.id}</td> 
          <td>{g.creatorWhite ? g.creator : g.opponent}</td> 
          <td>{!g.creatorWhite ? g.creator : g.opponent}</td> 
          <td>{g.creator}</td> 
          <td>{g.status}</td>
          <td><button onClick={() => callJoinGame(g.id)}>Join</button></td>
        </tr>)
    );
  };

  return <div>
    <div>Main</div>
    <div>
      <table>
        <thead>
          <tr>
            <th>game id</th>
            <th>White</th>
            <th>Black</th>
            <th>Creator</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {gameList(games)}
        </tbody>
      </table>
    
    </div>
    <button onClick={props.createGame}>Create new game</button>
  </div>
}

export default Main;