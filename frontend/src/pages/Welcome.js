import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import './Welcome.css';

const Welcome = () => {
  const [testStatus, setTestStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    checkTestStatus();
    // Poll every 5 seconds for test status
    const interval = setInterval(checkTestStatus, 5000);
    return () => clearInterval(interval);
  }, []);

  const checkTestStatus = async () => {
    try {
      const response = await api.get('/test/status');
      setTestStatus(response.data);
      setLoading(false);
      
      // If test is started, redirect to test page
      if (response.data.status === 'STARTED') {
        navigate('/test');
      }
    } catch (err) {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="loading">Loading...</div>
        </div>
      </>
    );
  }

  return (
    <>
      <Header />
      <div className="container">
        <div className="welcome-container">
          <div className="welcome-card">
            <h1>Welcome to the Test</h1>
            <div className="welcome-message">
              <p className="main-message">
                Welcome to the Test. Please wait for the admin to start the test.
              </p>
            </div>
            <div className="status-indicator">
              <div className={`status-dot ${testStatus?.status === 'STARTED' ? 'active' : 'waiting'}`}></div>
              <span className="status-text">
                {testStatus?.status === 'STARTED' ? 'Test Started' : 'Waiting for test to start...'}
              </span>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Welcome;
