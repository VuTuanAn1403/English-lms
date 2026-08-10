import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }, [user]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await api.post('/api/v1/auth/login', { email, password });
      const resData = response.data;
      if (resData.success) {
        const { token: jwtToken, user: userData } = resData.data;
        
        // Synchronously persist token and user to localStorage
        localStorage.setItem('token', jwtToken);
        localStorage.setItem('user', JSON.stringify(userData));

        setToken(jwtToken);
        setUser(userData);
        return { success: true, message: resData.message, user: userData };
      } else {
        return { success: false, message: resData.message || 'Đăng nhập thất bại' };
      }
    } catch (error) {
      const msg = error.response?.data?.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const register = async (fullName, email, password) => {
    setLoading(true);
    try {
      const response = await api.post('/api/v1/auth/register', { fullName, email, password });
      const resData = response.data;
      return { success: resData.success, message: resData.message || 'Đăng ký thành công' };
    } catch (error) {
      const msg = error.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại!';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const updateProfile = async (fullName, avatar) => {
    setLoading(true);
    try {
      const response = await api.put('/api/v1/users/profile', { fullName, avatar });
      const resData = response.data;
      if (resData.success) {
        localStorage.setItem('user', JSON.stringify(resData.data));
        setUser(resData.data);
        return { success: true, message: resData.message };
      }
      return { success: false, message: resData.message };
    } catch (error) {
      const msg = error.response?.data?.message || 'Cập nhật hồ sơ thất bại!';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateProfile }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
