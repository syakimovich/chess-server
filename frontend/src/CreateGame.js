import React from 'react';
import { Formik, Field, Form } from 'formik';

function CreateGame(props) {

  async function callCreateGame(params) {
    const createRequest = {creator: props.loggedInUser, creatorWhite: params.isCreatorWhite};
    const response = await fetch('/game/create', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      
      body: JSON.stringify(createRequest)
    });
    const newGameId = await response.text();
    props.openGame(newGameId);
  }

  return <div>
    <div>Create Game</div>
    <Formik
      initialValues={{
        isCreatorWhite: true
      }}
      onSubmit={callCreateGame}
    >
      <Form>
        <label htmlFor="isCreatorWhite">Play white</label>
        <Field type="checkbox" name="isCreatorWhite" />

        <button type="submit">Create</button>
      </Form>
    </Formik>
  </div>
}

export default CreateGame;