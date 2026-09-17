import axios from 'axios';

// Normalize Base URL: strip any trailing slashes to avoid double-slash route errors
const rawBaseUrl = import.meta.env.VITE_API_BASE_URL || (import.meta.env.PROD ? '' : 'http://localhost:8080');
export const API_BASE_URL = rawBaseUrl.replace(/\/+$/, '');

if (import.meta.env.PROD && !import.meta.env.VITE_API_BASE_URL) {
  console.warn(
    '[English LMS Warning] VITE_API_BASE_URL is not configured in production environment! ' +
    'Please configure VITE_API_BASE_URL in your Vercel Project Settings > Environment Variables.'
  );
}

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request Interceptor: Attach JWT Bearer Token & perform SAFE logging
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Safe request logging: HTTP method and endpoint only (NEVER log JWT, password, or payload)
    if (import.meta.env.DEV) {
      console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Safe status logging and global 401 Unauthorized handling
api.interceptors.response.use(
  (response) => {
    // Safe response logging: HTTP status and endpoint only
    if (import.meta.env.DEV) {
      console.log(`[API Response] ${response.status} ${response.config?.url}`);
    }
    return response;
  },
  (error) => {
    const status = error.response?.status;
    const endpoint = error.config?.url || 'UNKNOWN_ENDPOINT';
    
    // Safe error logging: HTTP status and endpoint only (NEVER log sensitive tokens or secrets)
    console.error(`[API Error] Status: ${status || 'NETWORK_ERROR'} on ${endpoint}`);

    // Global 401 Unauthorized handling
    if (status === 401) {
      const url = error.config?.url || '';
      const isAuthPath = url.includes('/auth/login') ||
        url.includes('/auth/register') ||
        url.includes('/user/login');

      // Do not redirect to login if user is browsing public pages
      const currentPath = window.location.pathname;
      const isPublicPath = currentPath === '/' || currentPath.startsWith('/courses');

      if (!isAuthPath && !isPublicPath) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        if (!currentPath.startsWith('/login') && !currentPath.startsWith('/admin/login')) {
          window.location.href = '/login';
        }
      }
    }

    return Promise.reject(error);
  }
);

export default api;

