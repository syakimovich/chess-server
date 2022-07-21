import React from 'react';
import { useFormik } from 'formik';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

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

  const formik = useFormik({
    initialValues: {
      isCreatorWhite: true,
    },
    onSubmit: values => {
      callCreateGame(values);
    },
  });

  return <Container>
    <Row>
      <Col>
        <h1>Create new Game</h1>
        <Form onSubmit={formik.handleSubmit}>
          <Form.Group className="mb-3" controlId="isCreatorWhite">
            <Form.Check type="checkbox" id="isCreatorWhite" name="isCreatorWhite" onChange={formik.handleChange} 
            checked={formik.values.isCreatorWhite} label="Play white" />
          </Form.Group>
          <Button variant="primary" type="submit">
            Create
          </Button>
        </Form>
      </Col>
    </Row>
  </Container>
}

export default CreateGame;