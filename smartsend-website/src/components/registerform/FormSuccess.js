import React from 'react';
import './Form.css';

const FormSuccess = () => {
  return (
    <div className='form-content-right'>
      <h1 className='form-success'>We have received your request!</h1>
      <img className='form-img-2' src= {require('../../images/svg-4.svg').default}alt='success-image' />
    </div>
  );
};

export default FormSuccess;