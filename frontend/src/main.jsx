import React from 'react';
import ReactDOM from 'react-dom/client';
import { CustomThemeProvider } from './contexts/ThemeContext';
import ErrorBoundary from './components/ErrorBoundary';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ErrorBoundary>
      <CustomThemeProvider>
        <App />
      </CustomThemeProvider>
    </ErrorBoundary>
  </React.StrictMode>
);
