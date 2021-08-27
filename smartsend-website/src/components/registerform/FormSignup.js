import { useState, React } from 'react';
import validate from './validateInfo';
import useForm from './useForm';
import './Form.css';
import PhoneInput from 'react-phone-number-input'
import 'react-phone-number-input/style.css'
import MultiSelect from "react-multi-select-component";

const FormSignup = ({ submitForm }) => {
  const { handleChange, handleSubmit, values, errors, loading } = useForm(
    submitForm,
    validate
  );
  
  const [phoneNumberValue, setPhoneNumber] = useState()

  const [daysSelected, setDaySelected] = useState([]);

  const [hoursSelected, setHourSelected] = useState([]);

  const daysOptions = [
    { label: "Sunday", value: "Sunday" },
    { label: "Monday", value: "Monday" },
    { label: "Tuesday", value: "Tuesday" },
    { label: "Wedensday", value: "Wedensday" },
    { label: "Thursday", value: "Thursday" },
    { label: "Friday", value: "Friday" },
    { label: "Saturday", value: "Saturday" },
  ];

  const hourOptions = [
    { label: "0:00 AM - 8:00 AM", value: "0-8" },
    { label: "8:00 AM - 16:00 PM", value: "8-16" },
    { label: "16:00 PM - 12:00 AM", value: "16-12" },
  ];

  const handleDaysChange = e => {
    setDaySelected(e);
    values.working_days = e;
  }

  const handleHoursChange = e => {
    setHourSelected(e);
    values.working_hours = e;
  }

  const handlePhoneNumberChange = e => {
    setPhoneNumber(e);
    values.contact_number = e;
  }

  return (
    <div className='form-content-right'>
      <form onSubmit={handleSubmit} className='form' noValidate>
        <h1>
          Become a SmartSend courrier today! Create your account by filling out the
          information below.
        </h1>
        <div className='form-inputs'>
        <label className='form-label'>First Name <mark>*</mark></label>
          <div className="name-container">
            <input
              className='form-input'
              type='text'
              name='first_name'
              placeholder='First Name'
              value={values.first_name}
              onChange={handleChange}
            />
            {errors.first_name && <p>{errors.first_name}</p>}
            <input
              className='form-input'
              type='text'
              name='last_name'
              placeholder='Last Name'
              value={values.last_name}
              onChange={handleChange}
            />
            {errors.last_name && <p>{errors.last_name}</p>}
          </div>
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Email <mark>*</mark></label>
          <input
            className='form-input'
            type='email'
            name='email'
            placeholder='Enter email'
            value={values.email}
            onChange={handleChange}
          />
          {errors.email && <p>{errors.email}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Password <mark>*</mark></label>
          <input
            className='form-input'
            type='password'
            name='password'
            placeholder='Enter password'
            value={values.password}
            onChange={handleChange}
          />
          {errors.password && <p>{errors.password}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Confirm Password <mark>*</mark></label>
          <input
            className='form-input'
            type='password'
            name='password2'
            placeholder='Confirm password'
            value={values.password2}
            onChange={handleChange}
          />
          {errors.password2 && <p>{errors.password2}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Date of Birth <mark>*</mark></label>
          <input
            className='form-input'
            type='date'
            name='birth_date'
            placeholder=''
            value={values.birth_date}
            onChange={handleChange}
          />
          {errors.birth_date && <p>{errors.birth_date}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Contact Number <mark>*</mark></label>
          <PhoneInput
            className='form-input'
            placeholder="Enter phone number"
            name='contact_number'
            country='IL'
            value={phoneNumberValue}
            onChange={handlePhoneNumberChange}
            onSubmit={function(){values.contact_number = phoneNumberValue}}/>
          {errors.contact_number && <p>{errors.contact_number}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Vehicle's License Plate Number (xxx-xxx-xxx)<mark>*</mark></label>
          <input
            className='form-input'
            type='text'             
            name='vehicle_number'
            placeholder='Enter vehicle number'
            value={values.vehicle_number}
            onChange={handleChange}
          />
          {errors.vehicle_number && <p>{errors.vehicle_number}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Select Working Days <mark>*</mark></label>
            <MultiSelect
            options={daysOptions}
            value={daysSelected}
            onChange={handleDaysChange}
            labelledBy="Select"
            />
          {errors.working_days && <p>{errors.working_days}</p>}
        </div>
        <div className='form-inputs'>
          <label className='form-label'>Select Working Hours <mark>*</mark></label>
            <MultiSelect
            options={hourOptions}
            value={hoursSelected}
            onChange={handleHoursChange}
            labelledBy="Select"
            />
          {errors.working_hours && <p>{errors.working_hours}</p>}
        </div>
                <div className='form-inputs'>
          <label className='form-label'>Profile Picture</label>
          <input
            className='form-input'
            type='file'
            accept="image/png, image/gif, image/jpeg" 
            name='profile_picture'
            placeholder='Choose profile picture'
            value={values.profile_picture}
            onChange={handleChange}
          />
          {errors.profile_picture && <p>{errors.profile_picture}</p>}
        </div>
        <div className='form-inputs'>
        <label className='form-label'><mark>*</mark> Required Fields</label>
        </div>
        <button disabled={loading} className='form-input-btn' type='submit'>
          Sign up
        </button>
        {loading && <div className="loading-label">Proccessing request...</div>}
        {/* <mark className='form-input-login'>
          Already have an account? Login <a href='/signin'>here</a>
        </mark> */}
      </form>
    </div>
  );
};

export default FormSignup;