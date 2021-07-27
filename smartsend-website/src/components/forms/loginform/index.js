import React, { useContext } from "react";
import {
  BoxContainer,
  FormContainer,
  Input,
  MutedLink,
  SubmitButton,
} from "../formelements";
import { Marginer } from "../marginer";

export function LoginForm(props) {
  return (
    <BoxContainer>
      <FormContainer>
        <Input type="email" placeholder="Email"/>
        <Input type="password" placeholder="Password"/>
      </FormContainer>
      <Marginer direction="vertical" margin={10} />
      <MutedLink href="#">Forget your password?</MutedLink>
      <Marginer direction="vertical" margin="1.6em" />
      <SubmitButton type="submit">Enter Account</SubmitButton>
      <Marginer direction="vertical" margin="1em" />
    </BoxContainer>
  );
}