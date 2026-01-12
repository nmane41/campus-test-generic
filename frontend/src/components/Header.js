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
        <h1>Young Element Placement Test</h1>
        <div className="header-actions">
          {user && (
            <>
              <span>Welcome, {user.username} ({user.role})</span>
              {user.role === 'ADMIN' && (
                <button className="btn btn-secondary" onClick={() => navigate('/admin')}>
                  Dashboard
                </button>
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

