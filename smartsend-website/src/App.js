import React from 'react'
import './App.css';
import Home from './pages/';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import SigninPage from './pages/signin';
import { AuthProvider } from './components/firebase/contexts/AuthContext';

function App() {
  return (
    <AuthProvider>
    <Router>
      <Switch>
        <Route path="/" component={Home} exact/>
        <Route path="/signin" component={SigninPage} exact/>
      </Switch>
    </Router>
    </AuthProvider>  
  );
}

export default App;
