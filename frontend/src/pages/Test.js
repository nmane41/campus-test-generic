import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import './Test.css';

const Test = () => {
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [questionTimes, setQuestionTimes] = useState({}); // questionId -> time in seconds
  const [currentQuestionId, setCurrentQuestionId] = useState(null);
  const [questionStartTime, setQuestionStartTime] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [timeRemaining, setTimeRemaining] = useState(3000); // 50 minutes in seconds
  const [testStartTime, setTestStartTime] = useState(null);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const timerIntervalRef = useRef(null);
  const questionTimerRef = useRef(null);
  const autoSubmitRef = useRef(false);

  useEffect(() => {
    loadQuestions();
    return () => {
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (testStartTime && !submitted && !submitting) {
      startTimer();
    }
    return () => {
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    };
  }, [testStartTime, submitted, submitting]);

  // Track time spent on current question
  useEffect(() => {
    if (currentQuestionId && !submitted && !submitting) {
      // Start tracking time for this question
      const startTime = Date.now();
      setQuestionStartTime(startTime);
      const questionIdRef = currentQuestionId; // Capture current question ID

      return () => {
        // Finalize time when leaving question
        const finalTime = Math.floor((Date.now() - startTime) / 1000);
        if (finalTime > 0) {
          setQuestionTimes(prev => {
            const baseTime = prev[questionIdRef] || 0;
            // Accumulate time (in case user revisits question)
            return {
              ...prev,
              [questionIdRef]: baseTime + finalTime
            };
          });
        }
      };
    }
  }, [currentQuestionId, submitted, submitting]);

  // Set first question as current when questions load
  useEffect(() => {
    if (questions.length > 0 && !currentQuestionId) {
      setCurrentQuestionId(questions[0].id);
    }
  }, [questions]);

  const startTimer = () => {
    if (timerIntervalRef.current) {
      clearInterval(timerIntervalRef.current);
    }

    timerIntervalRef.current = setInterval(() => {
      if (testStartTime) {
        const now = new Date().getTime();
        const start = new Date(testStartTime).getTime();
        const elapsed = Math.floor((now - start) / 1000);
        const remaining = Math.max(0, 3000 - elapsed);
        
        setTimeRemaining(remaining);

        if (remaining === 0 && !autoSubmitRef.current) {
          autoSubmitRef.current = true;
          handleAutoSubmit();
        }
      }
    }, 1000);
  };

  const handleAutoSubmit = async () => {
    if (submitting || submitted) return;
    
    // Finalize time for current question before submitting
    if (currentQuestionId && questionStartTime) {
      const finalTime = Math.floor((Date.now() - questionStartTime) / 1000);
      setQuestionTimes(prev => ({
        ...prev,
        [currentQuestionId]: (prev[currentQuestionId] || 0) + finalTime
      }));
    }
    
    setSubmitting(true);
    setError('');

    try {
      await api.post('/test/submit', { 
        answers,
        questionTimes: questionTimes
      });
      setSubmitted(true);
      setSubmitting(false);
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
      if (questionTimerRef.current) {
        clearInterval(questionTimerRef.current);
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit test');
      setSubmitting(false);
    }
  };

  const loadQuestions = async () => {
    try {
      // First check if test is started
      const statusResponse = await api.get('/test/status');
      
      if (statusResponse.data.status !== 'STARTED') {
        // Test not started, redirect to welcome
        navigate('/welcome');
        return;
      }
      
      const response = await api.get('/test/questions');
      setQuestions(response.data);
      
      // Get user's test attempt start time from status response
      if (statusResponse.data.userStartTime) {
        const startTimeStr = Array.isArray(statusResponse.data.userStartTime) 
          ? statusResponse.data.userStartTime.join('-') 
          : statusResponse.data.userStartTime;
        setTestStartTime(startTimeStr);
        localStorage.setItem('testStartTime', startTimeStr);
      } else {
        // Fallback: use current time (backend will have set the actual start time)
        // We'll sync on next status check
        const now = new Date().toISOString();
        setTestStartTime(now);
        localStorage.setItem('testStartTime', now);
      }
      
      setLoading(false);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load questions');
      setLoading(false);
      // If test not started, redirect to welcome
      if (err.response?.data?.error?.includes('not been started')) {
        navigate('/welcome');
      }
    }
  };

  const handleAnswerChange = (questionId, option) => {
    setAnswers({
      ...answers,
      [questionId]: option
    });
  };

  const handleQuestionView = (questionId) => {
    if (currentQuestionId !== questionId) {
      // Finalize time for previous question
      if (currentQuestionId && questionStartTime) {
        const finalTime = Math.floor((Date.now() - questionStartTime) / 1000);
        if (finalTime > 0) {
          setQuestionTimes(prev => ({
            ...prev,
            [currentQuestionId]: (prev[currentQuestionId] || 0) + finalTime
          }));
        }
      }
      
      // Set new current question
      setCurrentQuestionId(questionId);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (submitting || submitted) return;

    // Finalize time for current question before submitting
    if (currentQuestionId && questionStartTime) {
      const finalTime = Math.floor((Date.now() - questionStartTime) / 1000);
      setQuestionTimes(prev => ({
        ...prev,
        [currentQuestionId]: (prev[currentQuestionId] || 0) + finalTime
      }));
    }

    setSubmitting(true);
    setError('');

    try {
      await api.post('/test/submit', { 
        answers,
        questionTimes: questionTimes
      });
      setError('');
      setSubmitted(true);
      setSubmitting(false);
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
      if (questionTimerRef.current) {
        clearInterval(questionTimerRef.current);
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit test');
      setSubmitting(false);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="loading">Loading questions...</div>
        </div>
      </>
    );
  }

  if (submitted) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="submission-success">
            <div className="success-icon">✓</div>
            <h2>Test Submitted Successfully!</h2>
            <p className="success-message">
              Your test has been submitted successfully. 
              Your results will be reviewed by the administrators.
            </p>
            <p className="info-message">
              You will be notified about your results through the administrator.
            </p>
            <div className="success-actions">
              <button className="btn btn-primary" onClick={() => {
                logout();
                navigate('/login');
              }}>
                Logout
              </button>
            </div>
          </div>
        </div>
      </>
    );
  }

  if (error && !questions.length) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="error">{error}</div>
          <button className="btn btn-primary" onClick={() => navigate('/welcome')}>
            Back to Welcome
          </button>
        </div>
      </>
    );
  }

  return (
    <>
      <Header />
      <div className="container">
        <div className="test-header">
          <div>
            <h2>Young Element Placement Test</h2>
            <p>Total Questions: {questions.length}</p>
            <p>Answered: {Object.keys(answers).length} / {questions.length}</p>
          </div>
          <div className="timer-container">
            <div className={`timer ${timeRemaining < 300 ? 'timer-warning' : ''} ${timeRemaining === 0 ? 'timer-expired' : ''}`}>
              <span className="timer-label">Time Remaining:</span>
              <span className="timer-value">{formatTime(timeRemaining)}</span>
            </div>
          </div>
        </div>
        
        {error && <div className="error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          {questions.map((question, index) => (
            <div 
              key={question.id} 
              className="question-card"
              onMouseEnter={() => handleQuestionView(question.id)}
              onClick={() => handleQuestionView(question.id)}
            >
              <h3>Question {index + 1}</h3>
              <p className="question-text">{question.questionText}</p>
              <div className="options">
                <label>
                  <input
                    type="radio"
                    name={`question-${question.id}`}
                    value="A"
                    checked={answers[question.id] === 'A'}
                    onChange={() => handleAnswerChange(question.id, 'A')}
                  />
                  A. {question.optionA}
                </label>
                <label>
                  <input
                    type="radio"
                    name={`question-${question.id}`}
                    value="B"
                    checked={answers[question.id] === 'B'}
                    onChange={() => handleAnswerChange(question.id, 'B')}
                  />
                  B. {question.optionB}
                </label>
                <label>
                  <input
                    type="radio"
                    name={`question-${question.id}`}
                    value="C"
                    checked={answers[question.id] === 'C'}
                    onChange={() => handleAnswerChange(question.id, 'C')}
                  />
                  C. {question.optionC}
                </label>
                <label>
                  <input
                    type="radio"
                    name={`question-${question.id}`}
                    value="D"
                    checked={answers[question.id] === 'D'}
                    onChange={() => handleAnswerChange(question.id, 'D')}
                  />
                  D. {question.optionD}
                </label>
              </div>
            </div>
          ))}
          
          <div className="submit-section">
            <button
              type="submit"
              className="btn btn-success"
              disabled={submitting}
            >
              {submitting ? 'Submitting...' : 'Submit Test'}
            </button>
          </div>
        </form>
      </div>
    </>
  );
};

export default Test;

