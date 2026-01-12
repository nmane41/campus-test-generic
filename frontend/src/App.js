import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Test from './pages/Test';
import Welcome from './pages/Welcome';
import Results from './pages/Results';
import AdminDashboard from './pages/AdminDashboard';
import { AuthProvider, useAuth } from './context/AuthContext';
import './App.css';

const PrivateRoute = ({ children, requireAdmin = false }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="container">Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  if (requireAdmin && user.role !== 'ADMIN') {
    return <Navigate to="/test" />;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/welcome"
              element={
                <PrivateRoute>
                  <Welcome />
                </PrivateRoute>
              }
            />
            <Route
              path="/test"
              element={
                <PrivateRoute>
                  <Test />
                </PrivateRoute>
              }
            />
            <Route
              path="/results"
              element={
                <PrivateRoute requireAdmin={true}>
                  <Results />
                </PrivateRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <PrivateRoute requireAdmin={true}>
                  <AdminDashboard />
                </PrivateRoute>
              }
            />
            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;

