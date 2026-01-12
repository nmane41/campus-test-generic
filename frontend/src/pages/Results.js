import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Header from '../components/Header';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { CheckCircle2, AlertCircle, Trophy, Clock } from 'lucide-react';

const Results = () => {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex items-center justify-center min-h-[60vh]">
            <div className="flex flex-col items-center gap-4">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
              <p className="text-gray-600">Loading results...</p>
            </div>
          </div>
        </div>
      </>
    );
  }

  if (error) {
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
            </CardContent>
          </Card>
        </div>
      </>
    );
  }

  const percentage = result ? ((result.score / result.totalQuestions) * 100).toFixed(2) : 0;
  const passed = percentage >= 70;

  return (
    <>
      <Header />
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="max-w-3xl mx-auto">
          <Card className="shadow-soft-lg">
            <CardHeader className="text-center pb-4">
              <div className={`mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full ${
                passed ? 'bg-green-100' : 'bg-yellow-100'
              }`}>
                {passed ? (
                  <Trophy className="h-8 w-8 text-green-600" />
                ) : (
                  <AlertCircle className="h-8 w-8 text-yellow-600" />
                )}
              </div>
              <CardTitle className="text-2xl">Test Results</CardTitle>
              <CardDescription>Your test performance summary</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* Score Display */}
              <div className="text-center py-6 rounded-lg bg-gray-50">
                <div className="text-5xl font-bold text-gray-900 mb-2">{percentage}%</div>
                <div className="text-lg text-gray-600">
                  {result.score} out of {result.totalQuestions} questions
                </div>
                <Badge variant={passed ? 'success' : 'warning'} className="mt-3">
                  {passed ? 'Passed' : 'Needs Improvement'}
                </Badge>
              </div>

              {/* Details Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-start gap-3 p-4 rounded-lg bg-gray-50">
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-white">
                    <Clock className="h-5 w-5 text-gray-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600 mb-1">Start Time</p>
                    <p className="text-sm text-gray-900">{new Date(result.startTime).toLocaleString()}</p>
                  </div>
                </div>

                <div className="flex items-start gap-3 p-4 rounded-lg bg-gray-50">
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-white">
                    <Clock className="h-5 w-5 text-gray-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600 mb-1">End Time</p>
                    <p className="text-sm text-gray-900">{new Date(result.endTime).toLocaleString()}</p>
                  </div>
                </div>
              </div>

              {/* Message */}
              <div className={`rounded-lg p-4 ${
                passed ? 'bg-green-50 border border-green-200' : 'bg-yellow-50 border border-yellow-200'
              }`}>
                <div className="flex gap-3">
                  {passed ? (
                    <CheckCircle2 className="h-5 w-5 shrink-0 text-green-600 mt-0.5" />
                  ) : (
                    <AlertCircle className="h-5 w-5 shrink-0 text-yellow-600 mt-0.5" />
                  )}
                  <div>
                    <p className={`text-sm font-medium mb-1 ${
                      passed ? 'text-green-900' : 'text-yellow-900'
                    }`}>
                      {passed ? 'Congratulations!' : 'Keep Practicing'}
                    </p>
                    <p className={`text-sm ${
                      passed ? 'text-green-800' : 'text-yellow-800'
                    }`}>
                      {passed
                        ? 'You have passed the test. Your results will be reviewed by the administrators.'
                        : 'You can improve your score by practicing more. Keep up the good work!'}
                    </p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </>
  );
};

export default Results;
