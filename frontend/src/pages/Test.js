import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import { Button } from '../components/ui/button';
import { Card, CardContent } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { Clock, CheckCircle2, AlertCircle, Send } from 'lucide-react';
import { motion } from 'framer-motion';

const Test = () => {
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [questionTimes, setQuestionTimes] = useState({});
  const [currentQuestionId, setCurrentQuestionId] = useState(null);
  const [questionStartTime, setQuestionStartTime] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [timeRemaining, setTimeRemaining] = useState(3000);
  const [testStartTime, setTestStartTime] = useState(null);
  const { logout } = useAuth();
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

  useEffect(() => {
    if (currentQuestionId && !submitted && !submitting) {
      const startTime = Date.now();
      setQuestionStartTime(startTime);
      const questionIdRef = currentQuestionId;

      return () => {
        const finalTime = Math.floor((Date.now() - startTime) / 1000);
        if (finalTime > 0) {
          setQuestionTimes(prev => {
            const baseTime = prev[questionIdRef] || 0;
            return {
              ...prev,
              [questionIdRef]: baseTime + finalTime
            };
          });
        }
      };
    }
  }, [currentQuestionId, submitted, submitting]);

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
      const statusResponse = await api.get('/test/status');
      
      if (statusResponse.data.status !== 'STARTED') {
        navigate('/welcome');
        return;
      }
      
      const response = await api.get('/test/questions');
      setQuestions(response.data);
      
      if (statusResponse.data.userStartTime) {
        const startTimeStr = Array.isArray(statusResponse.data.userStartTime) 
          ? statusResponse.data.userStartTime.join('-') 
          : statusResponse.data.userStartTime;
        setTestStartTime(startTimeStr);
        localStorage.setItem('testStartTime', startTimeStr);
      } else {
        const now = new Date().toISOString();
        setTestStartTime(now);
        localStorage.setItem('testStartTime', now);
      }
      
      setLoading(false);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load questions');
      setLoading(false);
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
      if (currentQuestionId && questionStartTime) {
        const finalTime = Math.floor((Date.now() - questionStartTime) / 1000);
        if (finalTime > 0) {
          setQuestionTimes(prev => ({
            ...prev,
            [currentQuestionId]: (prev[currentQuestionId] || 0) + finalTime
          }));
        }
      }
      setCurrentQuestionId(questionId);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (submitting || submitted) return;

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

  const answeredCount = Object.keys(answers).length;
  const progress = questions.length > 0 ? (answeredCount / questions.length) * 100 : 0;

  if (loading) {
    return (
      <>
        <Header />
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex items-center justify-center min-h-[60vh]">
            <div className="flex flex-col items-center gap-4">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
              <p className="text-gray-600">Loading questions...</p>
            </div>
          </div>
        </div>
      </>
    );
  }

  if (submitted) {
    return (
      <>
        <Header />
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="max-w-2xl mx-auto">
            <Card className="shadow-soft-lg text-center">
              <CardContent className="pt-12 pb-12">
                <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
                  <CheckCircle2 className="h-8 w-8 text-green-600" />
                </div>
                <h2 className="text-2xl font-semibold mb-3">Test Submitted Successfully!</h2>
                <p className="text-gray-600 mb-8">
                  Your test has been submitted successfully. Your results will be reviewed by the administrators.
                </p>
                <Button onClick={() => {
                  logout();
                  navigate('/login');
                }}>
                  Logout
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </>
    );
  }

  if (error && !questions.length) {
    return (
      <>
        <Header />
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Card className="max-w-2xl mx-auto">
            <CardContent className="pt-6">
              <div className="flex items-center gap-3 text-red-600 mb-4">
                <AlertCircle className="h-5 w-5" />
                <span>{error}</span>
              </div>
              <Button onClick={() => navigate('/welcome')}>
                Back to Welcome
              </Button>
            </CardContent>
          </Card>
        </div>
      </>
    );
  }

  const currentQuestion = questions.find(q => q.id === currentQuestionId) || questions[0];

  return (
    <>
      <Header />
      <div className="min-h-screen bg-gray-50 pb-20">
        {/* Sticky Timer and Progress Bar */}
        <div className="sticky top-16 z-40 bg-white border-b border-gray-200 shadow-sm">
          <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex items-center justify-between gap-4 mb-3">
              <div className="flex items-center gap-4">
                <div className={`flex items-center gap-2 px-4 py-2 rounded-lg ${
                  timeRemaining < 300 ? 'bg-red-50 text-red-700' : 'bg-blue-50 text-blue-700'
                }`}>
                  <Clock className="h-4 w-4" />
                  <span className="font-mono font-semibold">{formatTime(timeRemaining)}</span>
                </div>
                <div className="text-sm text-gray-600">
                  {answeredCount} / {questions.length} answered
                </div>
              </div>
              <Button
                onClick={handleSubmit}
                disabled={submitting}
                className="gap-2"
              >
                <Send className="h-4 w-4" />
                {submitting ? 'Submitting...' : 'Submit Test'}
              </Button>
            </div>
            <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
              <motion.div
                className="h-full bg-blue-600"
                initial={{ width: 0 }}
                animate={{ width: `${progress}%` }}
                transition={{ duration: 0.3 }}
              />
            </div>
          </div>
        </div>

        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
            {/* Question Navigation Sidebar */}
            <div className="lg:col-span-1">
              <Card className="sticky top-32">
                <CardContent className="p-4">
                  <h3 className="text-sm font-semibold mb-3 text-gray-700">Questions</h3>
                  <div className="grid grid-cols-5 lg:grid-cols-1 gap-2 max-h-[60vh] overflow-y-auto">
                    {questions.map((question, index) => {
                      const isAnswered = answers[question.id];
                      const isCurrent = currentQuestionId === question.id;
                      return (
                        <button
                          key={question.id}
                          onClick={() => handleQuestionView(question.id)}
                          className={`flex items-center justify-center h-10 rounded-lg text-sm font-medium transition-all ${
                            isCurrent
                              ? 'bg-blue-600 text-white shadow-md'
                              : isAnswered
                              ? 'bg-green-100 text-green-700 hover:bg-green-200'
                              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                          }`}
                        >
                          {index + 1}
                        </button>
                      );
                    })}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Main Question Area */}
            <div className="lg:col-span-3">
              {error && (
                <div className="mb-4 flex items-center gap-2 rounded-lg bg-red-50 p-3 text-sm text-red-800">
                  <AlertCircle className="h-4 w-4" />
                  <span>{error}</span>
                </div>
              )}

              <form onSubmit={handleSubmit}>
                {currentQuestion && (
                  <motion.div
                    key={currentQuestion.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2 }}
                  >
                    <Card className="shadow-soft">
                      <CardContent className="p-6">
                        <div className="flex items-center justify-between mb-6">
                          <h2 className="text-xl font-semibold">
                            Question {questions.findIndex(q => q.id === currentQuestion.id) + 1} of {questions.length}
                          </h2>
                          {answers[currentQuestion.id] && (
                            <Badge variant="success" className="gap-1">
                              <CheckCircle2 className="h-3 w-3" />
                              Answered
                            </Badge>
                          )}
                        </div>
                        
                        <p className="text-gray-900 mb-6 text-lg leading-relaxed">
                          {currentQuestion.questionText}
                        </p>

                        <div className="space-y-3">
                          {['A', 'B', 'C', 'D'].map((option) => {
                            const optionText = currentQuestion[`option${option}`];
                            const isSelected = answers[currentQuestion.id] === option;
                            return (
                              <label
                                key={option}
                                className={`flex items-start gap-3 p-4 rounded-lg border-2 cursor-pointer transition-all ${
                                  isSelected
                                    ? 'border-blue-500 bg-blue-50'
                                    : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'
                                }`}
                              >
                                <input
                                  type="radio"
                                  name={`question-${currentQuestion.id}`}
                                  value={option}
                                  checked={isSelected}
                                  onChange={() => handleAnswerChange(currentQuestion.id, option)}
                                  className="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500"
                                />
                                <div className="flex-1">
                                  <span className="font-medium text-gray-700">{option}.</span>
                                  <span className="ml-2 text-gray-900">{optionText}</span>
                                </div>
                                {isSelected && (
                                  <CheckCircle2 className="h-5 w-5 text-blue-600 shrink-0 mt-0.5" />
                                )}
                              </label>
                            );
                          })}
                        </div>
                      </CardContent>
                    </Card>
                  </motion.div>
                )}

                {/* Navigation Buttons */}
                <div className="flex items-center justify-between mt-6">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => {
                      const currentIndex = questions.findIndex(q => q.id === currentQuestionId);
                      if (currentIndex > 0) {
                        handleQuestionView(questions[currentIndex - 1].id);
                      }
                    }}
                    disabled={questions.findIndex(q => q.id === currentQuestionId) === 0}
                  >
                    Previous
                  </Button>
                  <Button
                    type="button"
                    onClick={() => {
                      const currentIndex = questions.findIndex(q => q.id === currentQuestionId);
                      if (currentIndex < questions.length - 1) {
                        handleQuestionView(questions[currentIndex + 1].id);
                      }
                    }}
                    disabled={questions.findIndex(q => q.id === currentQuestionId) === questions.length - 1}
                  >
                    Next
                  </Button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Test;
