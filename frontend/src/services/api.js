import axios from 'axios';

// In Docker, nginx proxies /api to backend, so use relative URL
// In local dev (npm start), use full URL to backend
const getBaseURL = () => {
  if (process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }
  // Check if we're in development mode (React dev server typically runs on port 3000)
  // In production/Docker, nginx serves on port 80 and proxies /api
  const isDevelopment = window.location.port === '3000' || process.env.NODE_ENV === 'development';
  return isDevelopment ? 'http://localhost:8080/api' : '/api';
};

const api = axios.create({
  baseURL: getBaseURL(),
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

