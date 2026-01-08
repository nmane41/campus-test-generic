import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Header.css';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="header">
      <div className="header-content">
        <h1>Placement Test System</h1>
        <div className="header-actions">
          {user && (
            <>
              <span>Welcome, {user.username} ({user.role})</span>
              {user.role === 'ADMIN' && (
                <button className="btn btn-secondary" onClick={() => navigate('/admin')}>
                  Dashboard
                </button>
              )}
              {user.role === 'USER' && (
                <>
                  <button className="btn btn-secondary" onClick={() => navigate('/test')}>
                    Test
                  </button>
                  <button className="btn btn-secondary" onClick={() => navigate('/results')}>
                    Results
                  </button>
                </>
              )}
              <button className="btn btn-danger" onClick={handleLogout}>
                Logout
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Header;

