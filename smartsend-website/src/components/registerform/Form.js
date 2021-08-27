import React, { useState } from 'react';
import './Form.css';
import FormSignup from './FormSignup';
import FormSuccess from './FormSuccess';

const Form = () => {
  const [isSubmitted, setIsSubmitted] = useState(false);

  function submitForm() {
    console.log(isSubmitted)
    setIsSubmitted(true);
  }

  return (
    <>
      <div id='registeration' className='form-container'>
        <div className='form-content-left'>
          <img className='form-img' src= {require('../../images/svg-2.svg').default} alt='delivery' />
        </div>
        {!isSubmitted ? (
          <FormSignup submitForm={submitForm} />
        ) : (
          <FormSuccess />
        )}
      </div>
    </>
  );
};

export default Form;