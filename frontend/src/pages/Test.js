import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import './Test.css';

const Test = () => {
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadQuestions();
  }, []);

  const loadQuestions = async () => {
    try {
      const response = await api.get('/test/questions');
      setQuestions(response.data);
      setLoading(false);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load questions');
      setLoading(false);
    }
  };

  const handleAnswerChange = (questionId, option) => {
    setAnswers({
      ...answers,
      [questionId]: option
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (Object.keys(answers).length < questions.length) {
      if (!window.confirm('You have not answered all questions. Do you want to submit anyway?')) {
        return;
      }
    }

    setSubmitting(true);
    setError('');

    try {
      await api.post('/test/submit', { answers });
      navigate('/results');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit test');
      setSubmitting(false);
    }
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

  if (error && !questions.length) {
    return (
      <>
        <Header />
        <div className="container">
          <div className="error">{error}</div>
          <button className="btn btn-primary" onClick={() => navigate('/results')}>
            View Results
          </button>
        </div>
      </>
    );
  }

  return (
    <>
      <Header />
      <div className="container">
        <h2>Online Placement Test</h2>
        <p>Total Questions: {questions.length}</p>
        <p>Answered: {Object.keys(answers).length} / {questions.length}</p>
        
        {error && <div className="error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          {questions.map((question, index) => (
            <div key={question.id} className="question-card">
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

