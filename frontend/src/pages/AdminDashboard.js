import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Header from '../components/Header';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from '../components/ui/accordion';
import { 
  LayoutDashboard, 
  FileText, 
  Users, 
  BarChart3, 
  Download, 
  Plus, 
  Edit, 
  Trash2, 
  Play, 
  CheckCircle2, 
  XCircle, 
  Clock,
  AlertCircle,
  CheckCircle,
  User,
  Key,
  ToggleLeft,
  ToggleRight
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [stats, setStats] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [results, setResults] = useState([]);
  const [detailedResults, setDetailedResults] = useState([]);
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
    } else if (activeTab === 'summary-results') {
      loadResults();
    } else if (activeTab === 'detailed-results') {
      loadDetailedResults();
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
      loadDashboardStats();
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

  const tabs = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'questions', label: 'Questions', icon: FileText },
    { id: 'summary-results', label: 'Summary Results', icon: BarChart3 },
    { id: 'detailed-results', label: 'Detailed Results', icon: Users },
    { id: 'users', label: 'Users', icon: User },
  ];

  return (
    <>
      <Header />
      <div className="min-h-screen bg-gray-50">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Tabs */}
          <div className="mb-8 border-b border-gray-200">
            <nav className="flex space-x-1 overflow-x-auto">
              {tabs.map((tab) => {
                const Icon = tab.icon;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`flex items-center gap-2 px-4 py-3 text-sm font-medium transition-colors border-b-2 ${
                      activeTab === tab.id
                        ? 'border-blue-600 text-blue-600'
                        : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'
                    }`}
                  >
                    <Icon className="h-4 w-4" />
                    <span>{tab.label}</span>
                  </button>
                );
              })}
            </nav>
          </div>

          {/* Alerts */}
          <AnimatePresence>
            {error && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0 }}
                className="mb-6 flex items-center gap-2 rounded-lg bg-red-50 p-4 text-sm text-red-800"
              >
                <AlertCircle className="h-5 w-5" />
                <span>{error}</span>
              </motion.div>
            )}
            {success && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0 }}
                className="mb-6 flex items-center gap-2 rounded-lg bg-green-50 p-4 text-sm text-green-800"
              >
                <CheckCircle className="h-5 w-5" />
                <span>{success}</span>
              </motion.div>
            )}
          </AnimatePresence>

          {/* Dashboard Tab */}
          <AnimatePresence mode="wait">
            {activeTab === 'dashboard' && stats && (
              <motion.div
                key="dashboard"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
              >
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-semibold text-gray-900">Dashboard</h2>
                    <p className="text-gray-600 mt-1">Overview of test statistics</p>
                  </div>
                  <Button onClick={handleStartTest} className="gap-2">
                    <Play className="h-4 w-4" />
                    Start Test
                  </Button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                  <Card>
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-600">Total Users</p>
                          <p className="text-3xl font-semibold mt-2">{stats.totalUsers}</p>
                        </div>
                        <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center">
                          <Users className="h-6 w-6 text-blue-600" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-600">Active Users</p>
                          <p className="text-3xl font-semibold mt-2">{stats.activeUsers}</p>
                        </div>
                        <div className="h-12 w-12 rounded-lg bg-green-100 flex items-center justify-center">
                          <User className="h-6 w-6 text-green-600" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-600">Test Attempts</p>
                          <p className="text-3xl font-semibold mt-2">{stats.totalTestAttempts}</p>
                        </div>
                        <div className="h-12 w-12 rounded-lg bg-purple-100 flex items-center justify-center">
                          <FileText className="h-6 w-6 text-purple-600" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-600">Average Score</p>
                          <p className="text-3xl font-semibold mt-2">{stats.averageScore.toFixed(1)}%</p>
                        </div>
                        <div className="h-12 w-12 rounded-lg bg-yellow-100 flex items-center justify-center">
                          <BarChart3 className="h-6 w-6 text-yellow-600" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </motion.div>
            )}

            {/* Questions Tab */}
            {activeTab === 'questions' && (
              <motion.div
                key="questions"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
              >
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-semibold text-gray-900">Questions</h2>
                    <p className="text-gray-600 mt-1">Manage test questions</p>
                  </div>
                  <Button onClick={() => {
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
                  }} className="gap-2">
                    <Plus className="h-4 w-4" />
                    Add Question
                  </Button>
                </div>

                {showQuestionForm && (
                  <Card className="mb-6">
                    <CardHeader>
                      <CardTitle>{editingQuestion ? 'Edit Question' : 'Add New Question'}</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <form onSubmit={handleQuestionSubmit} className="space-y-4">
                        <div>
                          <label className="text-sm font-medium text-gray-700 mb-2 block">Question Text</label>
                          <textarea
                            value={questionForm.questionText}
                            onChange={(e) => setQuestionForm({ ...questionForm, questionText: e.target.value })}
                            required
                            className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                            rows={3}
                          />
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          {['A', 'B', 'C', 'D'].map((option) => (
                            <div key={option}>
                              <label className="text-sm font-medium text-gray-700 mb-2 block">Option {option}</label>
                              <input
                                type="text"
                                value={questionForm[`option${option}`]}
                                onChange={(e) => setQuestionForm({ ...questionForm, [`option${option}`]: e.target.value })}
                                required
                                className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                              />
                            </div>
                          ))}
                        </div>
                        <div>
                          <label className="text-sm font-medium text-gray-700 mb-2 block">Correct Option</label>
                          <select
                            value={questionForm.correctOption}
                            onChange={(e) => setQuestionForm({ ...questionForm, correctOption: e.target.value })}
                            required
                            className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                          >
                            <option value="A">A</option>
                            <option value="B">B</option>
                            <option value="C">C</option>
                            <option value="D">D</option>
                          </select>
                        </div>
                        <div className="flex gap-3">
                          <Button type="submit" disabled={loading}>
                            {loading ? 'Saving...' : editingQuestion ? 'Update' : 'Create'}
                          </Button>
                          <Button type="button" variant="outline" onClick={() => {
                            setShowQuestionForm(false);
                            setEditingQuestion(null);
                          }}>
                            Cancel
                          </Button>
                        </div>
                      </form>
                    </CardContent>
                  </Card>
                )}

                <div className="space-y-4">
                  {questions.map((question) => (
                    <Card key={question.id}>
                      <CardContent className="p-6">
                        <div className="flex items-start justify-between mb-4">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                              <Badge variant="secondary">#{question.id}</Badge>
                              {question.correctOption && (
                                <Badge variant="success">Correct: {question.correctOption}</Badge>
                              )}
                            </div>
                            <p className="text-gray-900 font-medium">{question.questionText}</p>
                          </div>
                          <div className="flex gap-2 ml-4">
                            <Button variant="ghost" size="sm" onClick={() => handleEditQuestion(question)}>
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button variant="ghost" size="sm" onClick={() => handleDeleteQuestion(question.id)} className="text-red-600 hover:text-red-700 hover:bg-red-50">
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-4">
                          {['A', 'B', 'C', 'D'].map((option) => (
                            <div key={option} className={`p-3 rounded-lg ${
                              question.correctOption === option ? 'bg-green-50 border border-green-200' : 'bg-gray-50'
                            }`}>
                              <span className="font-medium text-gray-700">{option}.</span>
                              <span className="ml-2 text-gray-900">{question[`option${option}`]}</span>
                              {question.correctOption === option && (
                                <CheckCircle2 className="h-4 w-4 text-green-600 inline-block ml-2" />
                              )}
                            </div>
                          ))}
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </motion.div>
            )}

            {/* Summary Results Tab */}
            {activeTab === 'summary-results' && (
              <motion.div
                key="summary-results"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
              >
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-semibold text-gray-900">Summary Results</h2>
                    <p className="text-gray-600 mt-1">Overview of all test attempts</p>
                  </div>
                  <Button onClick={handleDownloadPdf} className="gap-2">
                    <Download className="h-4 w-4" />
                    Download PDF
                  </Button>
                </div>

                <Card>
                  <CardContent className="p-0">
                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead>
                          <tr className="border-b border-gray-200 bg-gray-50">
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Username</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Email</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Score</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Time Taken</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Start Time</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">End Time</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                          {results.map((result) => (
                            <tr key={result.attemptId} className="hover:bg-gray-50 transition-colors">
                              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{result.username}</td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{result.email}</td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <Badge variant={result.score >= 70 ? 'success' : 'warning'}>{result.score}%</Badge>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{result.timeTakenFormatted || formatTimeTaken(result.timeTakenSeconds)}</td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{result.startTimeIST || formatToIST(result.startTime)}</td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{result.endTimeIST || formatToIST(result.endTime)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            )}

            {/* Detailed Results Tab */}
            {activeTab === 'detailed-results' && (
              <motion.div
                key="detailed-results"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
              >
                <div className="mb-6 flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-semibold text-gray-900">Detailed Results</h2>
                    <p className="text-gray-600 mt-1">Question-by-question breakdown for each user</p>
                  </div>
                  <Button onClick={handleDownloadDetailedZip} className="gap-2">
                    <Download className="h-4 w-4" />
                    Download ZIP
                  </Button>
                </div>

                <Accordion>
                  {detailedResults.map((result) => (
                    <AccordionItem key={result.userId} value={result.userId.toString()}>
                      <AccordionTrigger
                        isOpen={expandedUsers.has(result.userId)}
                        onClick={() => toggleUserExpansion(result.userId)}
                        className="hover:no-underline"
                      >
                        <div className="flex items-center justify-between w-full pr-4">
                          <div className="flex items-center gap-4">
                            <div>
                              <p className="font-semibold text-gray-900">{result.userName}</p>
                              <p className="text-sm text-gray-600">{result.email}</p>
                            </div>
                            <div className="flex items-center gap-4 text-sm">
                              <span className="text-gray-600">Score: <span className="font-medium text-gray-900">{result.score}%</span></span>
                              <span className="text-gray-600">Time: <span className="font-medium text-gray-900">{result.timeTaken}</span></span>
                            </div>
                          </div>
                        </div>
                      </AccordionTrigger>
                      <AccordionContent isOpen={expandedUsers.has(result.userId)}>
                        <Card className="mt-2">
                          <CardContent className="p-6">
                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                              <div>
                                <p className="text-xs font-medium text-gray-600 mb-1">Start Time</p>
                                <p className="text-sm text-gray-900">{result.startTimeIST}</p>
                              </div>
                              <div>
                                <p className="text-xs font-medium text-gray-600 mb-1">End Time</p>
                                <p className="text-sm text-gray-900">{result.endTimeIST}</p>
                              </div>
                              <div>
                                <p className="text-xs font-medium text-gray-600 mb-1">Time Taken</p>
                                <p className="text-sm text-gray-900">{result.timeTaken}</p>
                              </div>
                              <div>
                                <p className="text-xs font-medium text-gray-600 mb-1">Score</p>
                                <p className="text-sm font-semibold text-gray-900">{result.score}%</p>
                              </div>
                            </div>

                            <div className="space-y-3">
                              {result.answers.map((answer, index) => {
                                const isCorrect = answer.status === 'CORRECT';
                                const isIncorrect = answer.status === 'WRONG' || answer.status === 'INCORRECT';
                                
                                return (
                                  <div
                                    key={answer.questionId}
                                    className={`p-4 rounded-lg border-2 ${
                                      isCorrect
                                        ? 'bg-green-50 border-green-300'
                                        : isIncorrect
                                        ? 'bg-red-200 border-red-500'
                                        : 'bg-gray-50 border-gray-200'
                                    }`}
                                    style={
                                      isIncorrect 
                                        ? { backgroundColor: '#fecaca', borderColor: '#ef4444', borderWidth: '2px' }
                                        : isCorrect
                                        ? { backgroundColor: '#dcfce7', borderColor: '#86efac' }
                                        : {}
                                    }
                                  >
                                    <div className="flex items-start justify-between mb-3">
                                      <div className="flex items-center gap-2">
                                        <Badge variant="secondary">Q{index + 1}</Badge>
                                        <Badge
                                          variant={
                                            isCorrect ? 'success' : isIncorrect ? 'error' : 'secondary'
                                          }
                                        >
                                          {answer.status.replace('_', ' ')}
                                        </Badge>
                                        <div className="flex items-center gap-1 text-xs text-gray-600">
                                          <Clock className="h-3 w-3" />
                                          {formatTimeTaken(answer.timeTakenSeconds || 0)}
                                        </div>
                                      </div>
                                    </div>
                                    <p className="font-medium text-gray-900 mb-3">{answer.questionText}</p>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2 mb-3">
                                      {['A', 'B', 'C', 'D'].map((option) => {
                                        const isSelected = answer.selectedOption === option;
                                        const isCorrectOption = answer.correctOption === option;
                                        const isWrongSelected = isSelected && !isCorrectOption && isIncorrect;
                                        
                                        return (
                                          <div
                                            key={option}
                                            className={`p-2 rounded-lg text-sm ${
                                              isCorrectOption
                                                ? 'bg-green-200 border-2 border-green-500'
                                                : isWrongSelected
                                                ? 'bg-red-300 border-2 border-red-600'
                                                : 'bg-white border border-gray-200'
                                            }`}
                                            style={
                                              isWrongSelected
                                                ? { backgroundColor: '#fca5a5', borderColor: '#dc2626', borderWidth: '2px' }
                                                : isCorrectOption
                                                ? { backgroundColor: '#bbf7d0', borderColor: '#22c55e', borderWidth: '2px' }
                                                : {}
                                            }
                                          >
                                            <span className="font-medium">{option}.</span> {answer.options[option]}
                                            {isCorrectOption && (
                                              <CheckCircle2 className="h-4 w-4 text-green-600 inline-block ml-2" />
                                            )}
                                            {isWrongSelected && (
                                              <XCircle className="h-4 w-4 text-red-600 inline-block ml-2" />
                                            )}
                                          </div>
                                        );
                                      })}
                                    </div>
                                    <div className="text-xs text-gray-600 mt-2">
                                      Selected: <span className="font-medium">{answer.selectedOption || 'Not Answered'}</span> | 
                                      Correct: <span className="font-medium">{answer.correctOption}</span>
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                          </CardContent>
                        </Card>
                      </AccordionContent>
                    </AccordionItem>
                  ))}
                </Accordion>
              </motion.div>
            )}

            {/* Users Tab */}
            {activeTab === 'users' && (
              <motion.div
                key="users"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
              >
                <div className="mb-6">
                  <h2 className="text-2xl font-semibold text-gray-900">User Management</h2>
                  <p className="text-gray-600 mt-1">Manage user accounts and permissions</p>
                </div>

                <Card>
                  <CardContent className="p-0">
                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead>
                          <tr className="border-b border-gray-200 bg-gray-50">
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Username</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Email</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Role</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                          {users.map((user) => (
                            <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{user.id}</td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{user.username}</td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{user.email}</td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <Badge variant={user.role === 'ADMIN' ? 'info' : 'secondary'}>{user.role}</Badge>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <Badge variant={user.enabled ? 'success' : 'error'}>
                                  {user.enabled ? 'Enabled' : 'Disabled'}
                                </Badge>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm">
                                <div className="flex items-center gap-2">
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => handleResetPassword(user.id)}
                                    disabled={user.role === 'ADMIN'}
                                    className="gap-1"
                                  >
                                    <Key className="h-3 w-3" />
                                    Reset
                                  </Button>
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => handleToggleUserStatus(user.id, user.enabled)}
                                    disabled={user.role === 'ADMIN'}
                                    className={user.enabled ? 'text-red-600 hover:text-red-700 hover:bg-red-50' : 'text-green-600 hover:text-green-700 hover:bg-green-50'}
                                  >
                                    {user.enabled ? (
                                      <>
                                        <ToggleRight className="h-3 w-3" />
                                        Disable
                                      </>
                                    ) : (
                                      <>
                                        <ToggleLeft className="h-3 w-3" />
                                        Enable
                                      </>
                                    )}
                                  </Button>
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </>
  );
};

export default AdminDashboard;
