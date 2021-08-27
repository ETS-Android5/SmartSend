import { useState, useEffect } from 'react';
import { useAuth } from "../firebase/contexts/AuthContext"
import { database, auth } from "../firebase/firebase"
import moment from "moment";

const useForm = (callback, validate) => {
  const [values, setValues] = useState({
    first_name: '',
    last_name: '',
    email: '',
    password: '',
    password2: '',
    birth_date: '',
    contact_number: '',
    vehicle_number: '',
    profile_picture: '',
    created_date: '',
    id:'',
    lat: '',
    lng: '',
    status: '1',
    working_days: [],
    working_hours: []
  });

  const { signup } = useAuth();
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = e => {
    const { name, value } = e.target;
    setValues({
      ...values,
      [name]: value
    });
  };

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      setErrors('');
      if (Object.keys(validate(values)).length === 0) {
        setLoading(true)
        await signup(values.email, values.password).then((userCredential) => {
            var userID = userCredential.user.uid;

            console.log("signing up")
            values.id = userID;
            values.created_date = moment().format("DD-MM-YYYY hh:mm:ss");
            database.ref('riders/').child(userID).set(values);
            // console.log("entered to database")
        });
        // console.log(isSubmitting)
        setIsSubmitting(true);
      } else {
        setErrors(validate(values))
      }
    } catch {
      setErrors("Failed to create an account")
      setLoading(false);
    }
  };

  useEffect(
    () => {
      // console.log(isSubmitting)
      console.log(errors)
      //console.log(Object.keys(errors).length === 0)
      if (Object.keys(errors).length === 0 && isSubmitting) {
        setLoading(false);
        callback();
      }
    },
    [errors, isSubmitting]
  );

  return { handleChange, handleSubmit, values, errors, loading };
};

export default useForm;