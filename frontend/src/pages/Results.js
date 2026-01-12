import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import './Results.css';

const Results = () => {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadResults();
  }, []);

  const loadResults = async () => {
    try {
      const response = await api.get('/test/result');
      setResult(response.data);
      setLoading(false);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load results');
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="loading">Loading results...</div>
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="error">{error}</div>
          <button className="btn btn-primary" onClick={() => navigate('/test')}>
            Take Test
          </button>
        </div>
      </>
    );
  }

  const percentage = result ? ((result.score / result.totalQuestions) * 100).toFixed(2) : 0;

  return (
    <>
      <Header />
      <div className="container">
        <div className="results-card">
          <h2>Test Results</h2>
          <div className="result-details">
            <div className="result-item">
              <span className="label">Score:</span>
              <span className="value">{result.score} / {result.totalQuestions}</span>
            </div>
            <div className="result-item">
              <span className="label">Percentage:</span>
              <span className="value">{percentage}%</span>
            </div>
            <div className="result-item">
              <span className="label">Start Time:</span>
              <span className="value">{new Date(result.startTime).toLocaleString()}</span>
            </div>
            <div className="result-item">
              <span className="label">End Time:</span>
              <span className="value">{new Date(result.endTime).toLocaleString()}</span>
            </div>
          </div>
          <div className="result-message">
            {percentage >= 70 ? (
              <p className="success-message">Congratulations! You have passed the test.</p>
            ) : (
              <p className="info-message">You can improve your score by practicing more.</p>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Results;

