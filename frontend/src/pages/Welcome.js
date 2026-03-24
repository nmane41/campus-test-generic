import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import Header from '../components/Header';
import WaitingScreen from '../components/waiting-screens/WaitingScreen';

const Welcome = () => {
  const [testStatus, setTestStatus] = useState(null);
  const [loading, setLoading] = useState(true);
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
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex items-center justify-center min-h-[60vh]">
            <div className="flex flex-col items-center gap-4">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
              <p className="text-gray-600">Loading test information...</p>
            </div>
          </div>
        </div>
      </>
    );
  }

  const isStarted = testStatus?.status === 'STARTED';

  // If test is started, redirect (this should happen automatically, but just in case)
  if (isStarted) {
    navigate('/test');
    return null;
  }

  // Show premium waiting screen
  return (
    <>
      <Header />
      <WaitingScreen />
    </>
  );
};

export default Welcome;
