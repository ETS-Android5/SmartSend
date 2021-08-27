import firebase from "firebase"
import "firebase/auth"
import "firebase/database"

const config ={

    apiKey: "AIzaSyDj9HQUsLJPt8z2OfX-_Pzp9QD50-mDH7I",
    authDomain: "smartsend-792e3.firebaseapp.com",
    databaseURL: "https://smartsend-792e3-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "smartsend-792e3",
    storageBucket: "smartsend-792e3.appspot.com",
    messagingSenderId: "182679639571",
    appId: "1:182679639571:web:e79ec3506d15d7dc5a11e"
}

const app = firebase.initializeApp(config)
  
  export const auth = app.auth()
  export const database = app.database()
  export default app