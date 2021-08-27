import React, { useState, useRef } from "react";
import {
  BoxContainer,
  FormContainer,
  Input,
  MutedLink,
  SubmitButton,
  Alert
} from "../formelements";
import { Marginer } from "../marginer";
import { useAuth } from "../../firebase/contexts/AuthContext"
import { Link, useHistory } from "react-router-dom"

export function LoginForm(props) {
  const emailRef = useRef()
  const passwordRef = useRef()
  const { login } = useAuth()
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const history = useHistory()

  async function handleSubmit(e) {
    e.preventDefault()

    try {
      setError("")
      setLoading(true)
      await login(emailRef.current.value, passwordRef.current.value)
      history.push("/dashboard")
    } catch {
      setError("Failed to log in")
    }

    setLoading(false)
  }

  
  return (
    <BoxContainer>
      <FormContainer onSubmit={handleSubmit}>
        <Input id="email" type="email" placeholder="Email" ref={emailRef} required />
        <Input  id="password" type="password" placeholder="Password" ref={passwordRef} required/>
      <Marginer direction="vertical" margin={10} />
      <SubmitButton disabled={loading} type="submit">Login</SubmitButton>
      <Marginer direction="vertical" margin="1em" />
      </FormContainer>
      <MutedLink href="#">Forget your password?</MutedLink>
      <Marginer direction="vertical" margin="1em" />
      {error && <Alert variant="danger">{error}</Alert>}
    </BoxContainer>
  );
}