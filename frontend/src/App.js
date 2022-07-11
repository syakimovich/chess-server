import { useState } from "react";
import Main from './Main';
import Game from './Game';
import CreateGame from './CreateGame';

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
  return (
    <div className="App">
      {content}
    </div>
  );
}

export default App;