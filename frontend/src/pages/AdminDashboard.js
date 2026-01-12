import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import Header from '../components/Header';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [stats, setStats] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [results, setResults] = useState([]);
  const [detailedResults, setDetailedResults] = useState([]);
  const [resultsView, setResultsView] = useState('summary'); // 'summary' or 'detailed'
  const [expandedUsers, setExpandedUsers] = useState(new Set());
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showQuestionForm, setShowQuestionForm] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState(null);
  const [questionForm, setQuestionForm] = useState({
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctOption: 'A'
  });

  useEffect(() => {
    if (activeTab === 'dashboard') {
      loadDashboardStats();
    } else if (activeTab === 'questions') {
      loadQuestions();
    } else if (activeTab === 'results') {
      loadResults();
    } else if (activeTab === 'users') {
      loadUsers();
    }
  }, [activeTab]);

  useEffect(() => {
    if (activeTab === 'results' && resultsView === 'detailed') {
      loadDetailedResults();
    }
  }, [resultsView, activeTab]);

  const loadDashboardStats = async () => {
    try {
      const response = await api.get('/admin/dashboard');
      setStats(response.data);
    } catch (err) {
      setError('Failed to load dashboard stats');
    }
  };

  const loadQuestions = async () => {
    try {
      const response = await api.get('/admin/questions');
      setQuestions(response.data);
    } catch (err) {
      setError('Failed to load questions');
    }
  };

  const loadResults = async () => {
    try {
      const response = await api.get('/admin/results');
      setResults(response.data);
    } catch (err) {
      setError('Failed to load results');
    }
  };

  const loadDetailedResults = async () => {
    try {
      const response = await api.get('/admin/results/detailed');
      setDetailedResults(response.data);
    } catch (err) {
      setError('Failed to load detailed results');
    }
  };

  const handleDownloadDetailedZip = async () => {
    try {
      const response = await api.get('/admin/results/detailed/export/zip', {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `detailed-results-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      setSuccess('Detailed results ZIP downloaded successfully');
    } catch (err) {
      setError('Failed to download detailed results ZIP');
    }
  };

  const toggleUserExpansion = (userId) => {
    const newExpanded = new Set(expandedUsers);
    if (newExpanded.has(userId)) {
      newExpanded.delete(userId);
    } else {
      newExpanded.add(userId);
    }
    setExpandedUsers(newExpanded);
  };

  const handleDownloadPdf = async () => {
    try {
      const response = await api.get('/admin/results/export/pdf', {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `test-results-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      setSuccess('PDF downloaded successfully');
    } catch (err) {
      setError('Failed to download PDF');
    }
  };

  const handleStartTest = async () => {
    if (!window.confirm('Are you sure you want to start the test for all users? This action cannot be undone.')) {
      return;
    }

    try {
      await api.post('/admin/start-test');
      setSuccess('Test started successfully for all users');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to start test');
    }
  };

  const formatTimeTaken = (timeTakenSeconds) => {
    if (!timeTakenSeconds || timeTakenSeconds < 0) {
      return 'N/A';
    }
    const minutes = Math.floor(timeTakenSeconds / 60);
    const seconds = timeTakenSeconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  const formatToIST = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
      const date = new Date(dateTimeString);
      // Convert to IST (UTC+5:30)
      const istDate = new Date(date.toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));
      const options = { 
        year: 'numeric', 
        month: 'short', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true,
        timeZone: 'Asia/Kolkata'
      };
      return date.toLocaleString('en-US', options);
    } catch (e) {
      return dateTimeString;
    }
  };

  const loadUsers = async () => {
    try {
      const response = await api.get('/admin/users');
      setUsers(response.data);
    } catch (err) {
      setError('Failed to load users');
    }
  };

  const handleQuestionSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      if (editingQuestion) {
        await api.put(`/admin/questions/${editingQuestion.id}`, questionForm);
        setSuccess('Question updated successfully');
      } else {
        await api.post('/admin/questions', questionForm);
        setSuccess('Question created successfully');
      }
      setShowQuestionForm(false);
      setEditingQuestion(null);
      setQuestionForm({
        questionText: '',
        optionA: '',
        optionB: '',
        optionC: '',
        optionD: '',
        correctOption: 'A'
      });
      loadQuestions();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to save question');
    } finally {
      setLoading(false);
    }
  };

  const handleEditQuestion = (question) => {
    setEditingQuestion(question);
    setQuestionForm({
      questionText: question.questionText,
      optionA: question.optionA,
      optionB: question.optionB,
      optionC: question.optionC,
      optionD: question.optionD,
      correctOption: question.correctOption || 'A'
    });
    setShowQuestionForm(true);
  };

  const handleDeleteQuestion = async (id) => {
    if (!window.confirm('Are you sure you want to delete this question?')) {
      return;
    }

    try {
      await api.delete(`/admin/questions/${id}`);
      setSuccess('Question deleted successfully');
      loadQuestions();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to delete question');
    }
  };

  const handleResetPassword = async (userId) => {
    if (!window.confirm('Reset password for this user? A temporary password will be generated.')) {
      return;
    }

    try {
      const response = await api.post(`/admin/users/${userId}/reset-password`);
      alert(`Password reset successfully!\nTemporary Password: ${response.data.temporaryPassword}`);
      setSuccess('Password reset successfully');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to reset password');
    }
  };

  const handleToggleUserStatus = async (userId, currentStatus) => {
    const action = currentStatus ? 'disable' : 'enable';
    if (!window.confirm(`Are you sure you want to ${action} this user?`)) {
      return;
    }

    try {
      await api.put(`/admin/users/${userId}/status`, { enabled: !currentStatus });
      setSuccess(`User ${action}d successfully`);
      loadUsers();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update user status');
    }
  };

  return (
    <>
      <Header />
      <div className="container">
        <div className="admin-tabs">
          <button
            className={activeTab === 'dashboard' ? 'active' : ''}
            onClick={() => setActiveTab('dashboard')}
          >
            Dashboard
          </button>
          <button
            className={activeTab === 'questions' ? 'active' : ''}
            onClick={() => setActiveTab('questions')}
          >
            Questions
          </button>
          <button
            className={activeTab === 'results' ? 'active' : ''}
            onClick={() => setActiveTab('results')}
          >
            Results
          </button>
          <button
            className={activeTab === 'users' ? 'active' : ''}
            onClick={() => setActiveTab('users')}
          >
            Users
          </button>
        </div>

        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}

        {activeTab === 'dashboard' && stats && (
          <div>
            <div className="section-header">
              <h2>Dashboard</h2>
              <button
                className="btn btn-success"
                onClick={handleStartTest}
              >
                Start Test
              </button>
            </div>
            <div className="dashboard-stats">
              <div className="stat-card">
                <h3>Total Users</h3>
                <p className="stat-value">{stats.totalUsers}</p>
              </div>
              <div className="stat-card">
                <h3>Active Users</h3>
                <p className="stat-value">{stats.activeUsers}</p>
              </div>
              <div className="stat-card">
                <h3>Total Test Attempts</h3>
                <p className="stat-value">{stats.totalTestAttempts}</p>
              </div>
              <div className="stat-card">
                <h3>Average Score</h3>
                <p className="stat-value">{stats.averageScore.toFixed(2)}%</p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'questions' && (
          <div>
            <div className="section-header">
              <h2>Manage Questions</h2>
              <button
                className="btn btn-primary"
                onClick={() => {
                  setShowQuestionForm(true);
                  setEditingQuestion(null);
                  setQuestionForm({
                    questionText: '',
                    optionA: '',
                    optionB: '',
                    optionC: '',
                    optionD: '',
                    correctOption: 'A'
                  });
                }}
              >
                Add New Question
              </button>
            </div>

            {showQuestionForm && (
              <div className="card">
                <h3>{editingQuestion ? 'Edit Question' : 'Add New Question'}</h3>
                <form onSubmit={handleQuestionSubmit}>
                  <div className="form-group">
                    <label>Question Text:</label>
                    <textarea
                      value={questionForm.questionText}
                      onChange={(e) => setQuestionForm({ ...questionForm, questionText: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option A:</label>
                    <input
                      type="text"
                      value={questionForm.optionA}
                      onChange={(e) => setQuestionForm({ ...questionForm, optionA: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option B:</label>
                    <input
                      type="text"
                      value={questionForm.optionB}
                      onChange={(e) => setQuestionForm({ ...questionForm, optionB: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option C:</label>
                    <input
                      type="text"
                      value={questionForm.optionC}
                      onChange={(e) => setQuestionForm({ ...questionForm, optionC: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option D:</label>
                    <input
                      type="text"
                      value={questionForm.optionD}
                      onChange={(e) => setQuestionForm({ ...questionForm, optionD: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Correct Option:</label>
                    <select
                      value={questionForm.correctOption}
                      onChange={(e) => setQuestionForm({ ...questionForm, correctOption: e.target.value })}
                      required
                    >
                      <option value="A">A</option>
                      <option value="B">B</option>
                      <option value="C">C</option>
                      <option value="D">D</option>
                    </select>
                  </div>
                  <div className="form-actions">
                    <button type="submit" className="btn btn-primary" disabled={loading}>
                      {loading ? 'Saving...' : editingQuestion ? 'Update' : 'Create'}
                    </button>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowQuestionForm(false);
                        setEditingQuestion(null);
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            )}

            <div className="questions-list">
              <h3>All Questions ({questions.length})</h3>
              {questions.map((question) => (
                <div key={question.id} className="card question-item">
                  <div className="question-header">
                    <strong>Question #{question.id}</strong>
                    <div>
                      <button
                        className="btn btn-secondary"
                        onClick={() => handleEditQuestion(question)}
                      >
                        Edit
                      </button>
                      <button
                        className="btn btn-danger"
                        onClick={() => handleDeleteQuestion(question.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                  <p>{question.questionText}</p>
                  <div className="options-list">
                    <div>A. {question.optionA} {question.correctOption === 'A' && <span className="correct">✓ Correct</span>}</div>
                    <div>B. {question.optionB} {question.correctOption === 'B' && <span className="correct">✓ Correct</span>}</div>
                    <div>C. {question.optionC} {question.correctOption === 'C' && <span className="correct">✓ Correct</span>}</div>
                    <div>D. {question.optionD} {question.correctOption === 'D' && <span className="correct">✓ Correct</span>}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'results' && (
          <div>
            <div className="section-header">
              <h2>Student Results</h2>
              <div>
                <button
                  className={`btn ${resultsView === 'summary' ? 'btn-primary' : 'btn-secondary'}`}
                  onClick={() => {
                    setResultsView('summary');
                    loadResults();
                  }}
                  style={{ marginRight: '10px' }}
                >
                  Summary Results
                </button>
                <button
                  className={`btn ${resultsView === 'detailed' ? 'btn-primary' : 'btn-secondary'}`}
                  onClick={() => {
                    setResultsView('detailed');
                    loadDetailedResults();
                  }}
                  style={{ marginRight: '10px' }}
                >
                  Detailed Results
                </button>
                {resultsView === 'summary' && (
                  <button
                    className="btn btn-primary"
                    onClick={handleDownloadPdf}
                  >
                    Download Results (PDF)
                  </button>
                )}
                {resultsView === 'detailed' && (
                  <button
                    className="btn btn-primary"
                    onClick={handleDownloadDetailedZip}
                  >
                    Download Detailed Results (ZIP)
                  </button>
                )}
              </div>
            </div>

            {resultsView === 'summary' && (
              <table className="table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Score</th>
                    <th>Time Taken</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                  </tr>
                </thead>
                <tbody>
                  {results.map((result) => (
                    <tr key={result.attemptId}>
                      <td>{result.username}</td>
                      <td>{result.email}</td>
                      <td>{result.score}</td>
                      <td>{result.timeTakenFormatted || formatTimeTaken(result.timeTakenSeconds)}</td>
                      <td>{result.startTimeIST || formatToIST(result.startTime)}</td>
                      <td>{result.endTimeIST || formatToIST(result.endTime)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}

            {resultsView === 'detailed' && (
              <div className="detailed-results">
                {detailedResults.map((result) => (
                  <div key={result.userId} className="card detailed-result-card">
                    <div 
                      className="detailed-result-header"
                      onClick={() => toggleUserExpansion(result.userId)}
                      style={{ cursor: 'pointer', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                    >
                      <div>
                        <strong>{result.userName}</strong> ({result.email})
                        <span style={{ marginLeft: '20px', color: '#666' }}>
                          Score: {result.score} | Time: {result.timeTaken}
                        </span>
                      </div>
                      <span>{expandedUsers.has(result.userId) ? '▼' : '▶'}</span>
                    </div>
                    
                    {expandedUsers.has(result.userId) && (
                      <div className="detailed-result-content">
                        <div className="result-info">
                          <p><strong>Start Time:</strong> {result.startTimeIST} IST</p>
                          <p><strong>End Time:</strong> {result.endTimeIST} IST</p>
                          <p><strong>Time Taken:</strong> {result.timeTaken}</p>
                          <p><strong>Score:</strong> {result.score}</p>
                        </div>
                        
                        <table className="table detailed-answers-table">
                          <thead>
                            <tr>
                              <th>Q#</th>
                              <th>Question</th>
                              <th>Options</th>
                              <th>Selected</th>
                              <th>Correct</th>
                              <th>Status</th>
                              <th>Time Taken</th>
                            </tr>
                          </thead>
                          <tbody>
                            {result.answers.map((answer, index) => (
                              <tr key={answer.questionId}>
                                <td>{index + 1}</td>
                                <td>{answer.questionText}</td>
                                <td>
                                  <div>A. {answer.options.A}</div>
                                  <div>B. {answer.options.B}</div>
                                  <div>C. {answer.options.C}</div>
                                  <div>D. {answer.options.D}</div>
                                </td>
                                <td>{answer.selectedOption || 'Not Answered'}</td>
                                <td>{answer.correctOption}</td>
                                <td>
                                  <span className={`status-badge status-${answer.status.toLowerCase().replace('_', '-')}`}>
                                    {answer.status.replace('_', ' ')}
                                  </span>
                                </td>
                                <td>{formatTimeTaken(answer.timeTakenSeconds || 0)}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'users' && (
          <div>
            <h2>User Management</h2>
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.id}</td>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>{user.enabled ? 'Enabled' : 'Disabled'}</td>
                    <td>
                      <button
                        className="btn btn-secondary"
                        onClick={() => handleResetPassword(user.id)}
                        disabled={user.role === 'ADMIN'}
                      >
                        Reset Password
                      </button>
                      <button
                        className={user.enabled ? 'btn btn-danger' : 'btn btn-success'}
                        onClick={() => handleToggleUserStatus(user.id, user.enabled)}
                        disabled={user.role === 'ADMIN'}
                      >
                        {user.enabled ? 'Disable' : 'Enable'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
};

export default AdminDashboard;

