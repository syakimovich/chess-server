import React from 'react';
import { useState, useEffect } from 'react';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

function Main(props) {
  const [games, setGames] = useState([]);
  useEffect(() => {
    async function fetchData() {
      const response = await fetch('/game/listopen');
      const res = await response.json();
      setGames(res);
    }
    fetchData();
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
          <td><Button variant="primary" onClick={() => callJoinGame(g.id)}>Join</Button></td>
        </tr>)
    );
  };

  return <Container>
    <Row>
      <Col>
        <div>
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
              {gameList(games)}
            </tbody>
          </Table>
        
        </div>
        <Button variant="primary" onClick={props.createGame}>Create new game</Button>
      </Col>
    </Row>
    
  </Container>
}

export default Main;