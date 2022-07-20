import { useState } from "react";
import Main from './Main';
import Game from './Game';
import CreateGame from './CreateGame';
import 'bootstrap/dist/css/bootstrap.min.css';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Button from 'react-bootstrap/Button';

function App(props) {
  const [appState, setAppState] = useState({view: 'main'});
  const openGame = function(gameId) {setAppState({view: 'game', gameId: gameId})}
  const createGame = function() {setAppState({view: 'createGame'})}
  let content;
  if (appState.view === 'main') {
    content = <Main loggedInUser={props.loggedInUser} createGame={createGame} openGame={openGame} />;
  } else if (appState.view === 'game') {
    content = <Game loggedInUser={props.loggedInUser} gameId={appState.gameId} />;
  } else if (appState.view === 'createGame') {
    content = <CreateGame loggedInUser={props.loggedInUser} openGame={openGame} />;
  } else {
    content = <div>Error</div>
  }

  async function logout() {
    await fetch('/logout', {
      method: 'POST'
    });
    window.location.reload();
  }
  return (
    <div className="App">
      <Navbar>
        <Container>
          <Navbar.Brand>Chess server</Navbar.Brand>
          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Text>
              Signed in as: {props.loggedInUser} <Button onClick={logout}>Logout</Button>
            </Navbar.Text>
          </Navbar.Collapse>
        </Container>
      </Navbar>
      {content}
    </div>
  );
}

export default App;