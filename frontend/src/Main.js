import React from 'react';
import { useState, useEffect } from 'react';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

function Main(props) {
  const [openGames, setOpenGames] = useState([]);
  const [myGames, setMyGames] = useState([]);
  const [curView, setCurView] = useState('OPEN_GAMES');
  useEffect(() => {
    async function fetchOpenGames() {
      const response = await fetch('/game/listopen');
      const res = await response.json();
      setOpenGames(res);
    }
    fetchOpenGames();
    async function fetchMyGames() {
      const response = await fetch('/game/list-by-username?' + new URLSearchParams({
        username: props.loggedInUser
    }));
      const res = await response.json();
      setMyGames(res);
    }
    fetchMyGames();
  }, []);

  async function callJoinGame(gameId) {
    await fetch('/game/join', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({id: gameId, opponent: props.loggedInUser})
    });
    props.openGame(gameId);
  }

  const gameList = function(games) {
    return games.map(
      (g) => (<tr key={g.id}>
          <td>{g.id}</td> 
          <td>{g.creatorWhite ? g.creator : g.opponent}</td> 
          <td>{!g.creatorWhite ? g.creator : g.opponent}</td> 
          <td>{g.creator}</td> 
          <td>{g.status}</td>
          <td>
          {curView === 'OPEN_GAMES' && <Button variant="primary" onClick={() => callJoinGame(g.id)}>Join</Button>}
          </td>
        </tr>)
    );
  };

  return <Container>
    <Row>
      <Col>
        <div>
          {curView !== 'OPEN_GAMES' && <Button variant="primary" onClick={() => setCurView('OPEN_GAMES')}>Games open to join</Button>}
          {curView !== 'MY_GAMES' && <Button variant="primary" onClick={() => setCurView('MY_GAMES')}>My games</Button>}
          {curView === 'OPEN_GAMES' && <h1>Games open to join</h1>}
          {curView === 'MY_GAMES' && <h1>My games</h1>}
          <Table bordered>
            <thead>
              <tr>
                <th>Game id</th>
                <th>White</th>
                <th>Black</th>
                <th>Creator</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {gameList(curView === 'OPEN_GAMES' ? openGames : myGames)}
            </tbody>
          </Table>
        
        </div>
        <Button variant="primary" onClick={props.createGame}>Create new game</Button>
      </Col>
    </Row>
    
  </Container>
}

export default Main;