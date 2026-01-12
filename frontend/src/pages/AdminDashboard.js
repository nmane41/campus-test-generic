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
            <h2>Student Results</h2>
            <table className="table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Score</th>
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
                    <td>{new Date(result.startTime).toLocaleString()}</td>
                    <td>{new Date(result.endTime).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
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

