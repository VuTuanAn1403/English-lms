import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { Box } from '@mui/material';
import { AuthProvider, useAuth } from './contexts/AuthContext';

// Client Public Components
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Footer from './components/Footer';

// Client Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Courses from './pages/Courses';
import CourseDetail from './pages/CourseDetail';
import LessonView from './pages/LessonView';
import MyCourses from './pages/MyCourses';
import Profile from './pages/Profile';
import AiAssistant from './pages/AiAssistant';
import Checkout from './pages/Checkout';
import MockCheckoutPage from './pages/MockCheckoutPage';
import PaymentResult from './pages/PaymentResult';
import MyOrders from './pages/MyOrders';
import NotFound from './pages/NotFound';

// Admin Components & Pages
import AdminProtectedRoute from './components/admin/AdminProtectedRoute';
import AdminLayout from './components/admin/AdminLayout';
import AdminLogin from './pages/admin/AdminLogin';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminUsers from './pages/admin/AdminUsers';
import AdminUserDetail from './pages/admin/AdminUserDetail';
import AdminCourses from './pages/admin/AdminCourses';
import AdminCourseForm from './pages/admin/AdminCourseForm';
import AdminLessons from './pages/admin/AdminLessons';
import AdminEnrollments from './pages/admin/AdminEnrollments';
import AdminRevenue from './pages/admin/AdminRevenue';
import AdminAi from './pages/admin/AdminAi';
import AdminProfile from './pages/admin/AdminProfile';
import AdminSettings from './pages/admin/AdminSettings';

const ClientProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const ClientLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();
  const isAiPage = location.pathname === '/ai-assistant';

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: isAiPage ? '100vh' : 'auto',
        minHeight: '100vh',
        bgcolor: 'background.default',
        overflow: isAiPage ? 'hidden' : 'visible'
      }}
    >
      <Navbar onToggleSidebar={() => setSidebarOpen(true)} />
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          minHeight: 0,
          overflow: isAiPage ? 'hidden' : 'visible'
        }}
      >
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/courses" element={<Courses />} />
          <Route path="/courses/:id" element={<CourseDetail />} />
          <Route
            path="/courses/:courseId/lessons/:lessonId"
            element={
              <ClientProtectedRoute>
                <LessonView />
              </ClientProtectedRoute>
            }
          />
          <Route path="/payment/mock-checkout" element={<MockCheckoutPage />} />
          <Route path="/payment/result" element={<PaymentResult />} />

          <Route
            path="/checkout/:courseId"
            element={
              <ClientProtectedRoute>
                <Checkout />
              </ClientProtectedRoute>
            }
          />
          <Route
            path="/my-courses"
            element={
              <ClientProtectedRoute>
                <MyCourses />
              </ClientProtectedRoute>
            }
          />
          <Route
            path="/my-orders"
            element={
              <ClientProtectedRoute>
                <MyOrders />
              </ClientProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ClientProtectedRoute>
                <Profile />
              </ClientProtectedRoute>
            }
          />
          <Route
            path="/ai-assistant"
            element={
              <ClientProtectedRoute>
                <AiAssistant />
              </ClientProtectedRoute>
            }
          />

          <Route path="*" element={<NotFound />} />
        </Routes>
      </Box>

      {!isAiPage && <Footer />}
    </Box>
  );
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Admin Login Route */}
          <Route path="/admin/login" element={<AdminLogin />} />

          {/* Admin Protected Dashboard Routes */}
          <Route
            path="/admin"
            element={
              <AdminProtectedRoute>
                <AdminLayout />
              </AdminProtectedRoute>
            }
          >
            <Route index element={<AdminDashboard />} />
            <Route path="users" element={<AdminUsers />} />
            <Route path="users/:id" element={<AdminUserDetail />} />
            <Route path="courses" element={<AdminCourses />} />
            <Route path="courses/new" element={<AdminCourseForm />} />
            <Route path="courses/edit/:id" element={<AdminCourseForm />} />
            <Route path="lessons" element={<AdminLessons />} />
            <Route path="enrollments" element={<AdminEnrollments />} />
            <Route path="revenue" element={<AdminRevenue />} />
            <Route path="ai" element={<AdminAi />} />
            <Route path="profile" element={<AdminProfile />} />
            <Route path="settings" element={<AdminSettings />} />
          </Route>

          {/* Client Routes */}
          <Route path="/*" element={<ClientLayout />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
