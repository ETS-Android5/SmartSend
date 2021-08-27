export default function validateInfo(values) {
  let errors = {};
  var min_length = 6;

  if (values.first_name === undefined || values.first_name === "" || !values.first_name.trim()) {
    errors.first_name = 'First name required';
  }
  else if (!/^[a-z ,.'-]+$/i.test(values.first_name.trim())) {
    errors.first_name = 'Enter a valid first name';
  }

  if (values.last_name === undefined || values.last_name === "" || !values.last_name.trim()) {
    errors.last_name = 'Last name required';
  }
  else if (!/^[a-z ,.'-]+$/i.test(values.last_name.trim())) {
    errors.last_name = 'Enter a valid last name';
  }

  if (values.email === undefined || values.email === "" || !values.email) {
    errors.email = 'Email required';
  } else if (!/\S+@\S+\.\S+/.test(values.email)) {
    errors.email = 'Email address is invalid';
  }
  if (values.password === undefined || values.password === "" || !values.password) {
    errors.password = 'Password is required';
  } else if (values.password.length < min_length) {
    errors.password = 'Password needs to be 6 characters or more';
  }

  if (values.password2 === undefined || values.password2 === "" || !values.password2) {
    errors.password2 = 'Password is required';
  } else if (values.password2 !== values.password) {
    errors.password2 = 'Passwords do not match';
  }

  if (values.birth_date === undefined || values.birth_date === "" || !values.birth_date) {
    errors.birth_date = 'Date of birth is required';
  }

  if (values.vehicle_number === undefined || values.vehicle_number === "" || !values.vehicle_number) {
    errors.vehicle_number = 'Date of birth is required';
  } else if (!/^\d\d\d-\d\d\d-\d\d\d$/.test(values.vehicle_number)) {
    errors.vehicle_number = 'Enter a valid license plate number';
  }

  if (values.contact_number === undefined || values.contact_number === "" || !values.contact_number) {
    errors.contact_number = 'Contact number is required';
  } else if (!/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/.test(values.contact_number)) {
    errors.contact_number = 'Enter a valid contact number';
  }

  if (values.working_days === undefined || values.working_days === [] || !values.working_days || values.working_days.length === 0) {
    errors.working_days = 'Working days is required';
  }

  if (values.working_hours === undefined || values.working_hours === [] || !values.working_hours || values.working_hours.length === 0) {
    errors.working_hours = 'Working hours is required';
  }

  return errors;
}