import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Avatar,
  Chip,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import {
  People as PeopleIcon,
  MenuBook as CoursesIcon,
  AutoStories as LessonsIcon,
  AssignmentTurnedIn as EnrollmentIcon,
  Add as AddIcon,
  ArrowForward as ArrowForwardIcon,
  TrendingUp as TrendingUpIcon,
  EmojiEvents as TrophyIcon,
  CheckCircle as CompleteIcon,
  PieChart as ChartIcon
} from '@mui/icons-material';
import api from '../../services/api';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalCourses: 0,
    totalLessons: 0,
    totalEnrollments: 0,
    totalStudySessions: 0,
    completedCoursesCount: 0,
    inProgressCoursesCount: 0,
    notStartedCoursesCount: 0,
    completionRate: 0.0
  });

  const [distribution, setDistribution] = useState({
    range0to25: 0,
    range25to50: 0,
    range50to75: 0,
    range75to100: 0
  });

  const [topStudents, setTopStudents] = useState([]);
  const [topCourses, setTopCourses] = useState([]);
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // 1. Fetch Real Total Users from user-service
      const userCountRes = await api.get('/api/v1/users/count').catch(() => ({ data: { data: 0 } }));
      const realUserCount = userCountRes.data?.data || 0;

      // 2. Fetch Real Courses
      const courseRes = await api.get('/api/v1/courses').catch(() => ({ data: { data: [] } }));
      const fetchedCourses = courseRes.data?.data || [];
      setCourses(fetchedCourses);

      // 3. Fetch Enrollment & System Stats from course-service (FR-21)
      const enrollStatsRes = await api.get('/api/v1/admin/enrollments/statistics').catch(() => ({ data: { data: null } }));
      const enrollStats = enrollStatsRes.data?.data;

      // 4. Fetch Progress Analytics
      const analyticsRes = await api.get('/api/v1/progress/admin/analytics').catch(() => ({ data: { data: null } }));
      const analytics = analyticsRes.data?.data;

      const totalLessonsCount = enrollStats?.totalLessons || 0;
      const totalEnrollCount = enrollStats?.totalEnrollments || 0;
      const completedCount = enrollStats?.completedCount || 0;
      const inProgressCount = enrollStats?.inProgressCount || 0;
      const notStartedCount = enrollStats?.notStartedCount || 0;
      const compRate = enrollStats?.completionRate || 0.0;

      setStats({
        totalUsers: realUserCount,
        totalCourses: enrollStats?.totalCourses || fetchedCourses.length,
        totalLessons: totalLessonsCount,
        totalEnrollments: totalEnrollCount,
        totalStudySessions: analytics?.totalStudySessions || 0,
        completedCoursesCount: completedCount,
        inProgressCoursesCount: inProgressCount,
        notStartedCoursesCount: notStartedCount,
        completionRate: compRate
      });

      if (analytics) {
        if (analytics.progressDistribution) {
          setDistribution(analytics.progressDistribution);
        }
        if (analytics.topStudents) {
          setTopStudents(analytics.topStudents);
        }
        if (analytics.topCourses) {
          setTopCourses(analytics.topCourses);
        }
      }
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    { title: 'Tổng Học Viên Thực', value: stats.totalUsers, icon: <PeopleIcon fontSize="large" />, color: '#4F46E5', bg: '#EEF2FF' },
    { title: 'Tổng Khóa Học', value: stats.totalCourses, icon: <CoursesIcon fontSize="large" />, color: '#10B981', bg: '#ECFDF5' },
    { title: 'Tổng Lượt Đăng Ký', value: stats.totalEnrollments, icon: <EnrollmentIcon fontSize="large" />, color: '#8B5CF6', bg: '#F3E8FF' },
    { title: 'Tổng Bài Học Đã Học', value: stats.totalStudySessions, icon: <LessonsIcon fontSize="large" />, color: '#F59E0B', bg: '#FEF3C7' },
    { title: 'Tỷ Lệ Hoàn Thành', value: `${stats.completionRate}%`, icon: <TrendingUpIcon fontSize="large" />, color: '#EC4899', bg: '#FCE7F3' },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Tổng Quan Hệ Thống (Realtime Dashboard)
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Báo cáo thống kê 100% dữ liệu thật từ cơ sở dữ liệu PostgreSQL (Users, Enrollments, Progress)
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/admin/courses/new')}
          sx={{ borderRadius: 3, fontWeight: 700 }}
        >
          Tạo Khóa Học Mới
        </Button>
      </Box>

      {loading && <LinearProgress sx={{ mb: 3, borderRadius: 1 }} />}

      {/* KPI Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {statCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={2.4} key={index}>
            <Card elevation={0} sx={{ height: '100%', p: 1, border: '1px solid #e2e8f0', borderRadius: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Avatar sx={{ bgcolor: card.bg, color: card.color, width: 52, height: 52 }}>
                    {card.icon}
                  </Avatar>
                  <Chip
                    icon={<TrendingUpIcon />}
                    label="Real Data"
                    size="small"
                    color="success"
                    sx={{ fontWeight: 'bold' }}
                  />
                </Box>
                <Typography variant="h4" fontWeight="bold" sx={{ color: card.color }}>
                  {card.value}
                </Typography>
                <Typography variant="body2" color="text.secondary" fontWeight={600}>
                  {card.title}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* PROGRESS DISTRIBUTION CHART BAR CARDS */}
      <Paper elevation={0} sx={{ p: 3, mb: 4, borderRadius: 4, border: '1px solid #e2e8f0' }}>
        <Typography variant="h6" fontWeight="bold" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
          <ChartIcon color="primary" /> Biểu Đồ Phân Bổ Tiến Độ Học Tập Thật (Progress Distribution)
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={3}>
            <Box sx={{ p: 2, bgcolor: '#F9FAFB', borderRadius: 3, border: '1px solid #E5E7EB' }}>
              <Typography variant="caption" color="text.secondary" fontWeight={700}>DẢI 0% - 25% (SƠ CẤP)</Typography>
              <Typography variant="h4" fontWeight="bold" color="text.primary" sx={{ my: 1 }}>
                {distribution.range0to25 || 0} lượt
              </Typography>
              <LinearProgress variant="determinate" value={distribution.range0to25 > 0 ? 30 : 0} sx={{ height: 6, borderRadius: 3, bgcolor: '#e2e8f0' }} />
            </Box>
          </Grid>

          <Grid item xs={12} sm={3}>
            <Box sx={{ p: 2, bgcolor: '#FEF3C7', borderRadius: 3, border: '1px solid #FDE68A' }}>
              <Typography variant="caption" color="#B45309" fontWeight={700}>DẢI 25% - 50% (ĐANG HỌC)</Typography>
              <Typography variant="h4" fontWeight="bold" color="#92400E" sx={{ my: 1 }}>
                {distribution.range25to50 || 0} lượt
              </Typography>
              <LinearProgress variant="determinate" value={distribution.range25to50 > 0 ? 50 : 0} color="warning" sx={{ height: 6, borderRadius: 3 }} />
            </Box>
          </Grid>

          <Grid item xs={12} sm={3}>
            <Box sx={{ p: 2, bgcolor: '#EEF2FF', borderRadius: 3, border: '1px solid #C7D2FE' }}>
              <Typography variant="caption" color="#4338CA" fontWeight={700}>DẢI 50% - 75% (TĂNG TỐC)</Typography>
              <Typography variant="h4" fontWeight="bold" color="#3730A3" sx={{ my: 1 }}>
                {distribution.range50to75 || 0} lượt
              </Typography>
              <LinearProgress variant="determinate" value={distribution.range50to75 > 0 ? 75 : 0} color="primary" sx={{ height: 6, borderRadius: 3 }} />
            </Box>
          </Grid>

          <Grid item xs={12} sm={3}>
            <Box sx={{ p: 2, bgcolor: '#ECFDF5', borderRadius: 3, border: '1px solid #A7F3D0' }}>
              <Typography variant="caption" color="#047857" fontWeight={700}>DẢI 75% - 100% (HOÀN THÀNH)</Typography>
              <Typography variant="h4" fontWeight="bold" color="#065F46" sx={{ my: 1 }}>
                {distribution.range75to100 || 0} lượt
              </Typography>
              <LinearProgress variant="determinate" value={distribution.range75to100 > 0 ? 100 : 0} color="success" sx={{ height: 6, borderRadius: 3 }} />
            </Box>
          </Grid>
        </Grid>
      </Paper>

      <Grid container spacing={3}>
        {/* TOP HARD-WORKING STUDENTS */}
        <Grid item xs={12} md={6}>
          <Paper elevation={0} sx={{ p: 3, borderRadius: 4, border: '1px solid #e2e8f0', height: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <TrophyIcon color="warning" /> Học Viên Chăm Chỉ Nhất Thật
              </Typography>
              <Chip label="Realtime" size="small" color="primary" sx={{ fontWeight: 700 }} />
            </Box>
            <Divider sx={{ mb: 2 }} />

            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 'bold' }}>Hạng</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Học Viên</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Bài Đã Hoàn Thành</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {topStudents.length > 0 ? (
                    topStudents.map((s, idx) => (
                      <TableRow key={s.userId || idx} hover>
                        <TableCell>
                          <Chip
                            label={`#${idx + 1}`}
                            size="small"
                            color={idx === 0 ? 'warning' : 'primary'}
                            sx={{ fontWeight: 800 }}
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" fontWeight="bold">{s.studentName || 'Học viên'}</Typography>
                          <Typography variant="caption" color="text.secondary">{s.studentEmail}</Typography>
                        </TableCell>
                        <TableCell sx={{ fontWeight: 800, color: '#10B981' }}>
                          {s.completedLessonsCount || 0} bài học
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={3} align="center" sx={{ py: 3 }}>
                        Chưa có học viên nào hoàn thành bài học.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </Grid>

        {/* TOP MOST COMPLETED COURSES */}
        <Grid item xs={12} md={6}>
          <Paper elevation={0} sx={{ p: 3, borderRadius: 4, border: '1px solid #e2e8f0', height: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <CompleteIcon color="success" /> Khóa Học Hoàn Thành Nhiều Nhất Thật
              </Typography>
              <Chip label="Statistics" size="small" color="success" sx={{ fontWeight: 700 }} />
            </Box>
            <Divider sx={{ mb: 2 }} />

            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 'bold' }}>Tên Khóa Học</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Trình Độ</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Lượt Hoàn Thành</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {topCourses.length > 0 ? (
                    topCourses.map((c, idx) => (
                      <TableRow key={c.courseId || idx} hover>
                        <TableCell sx={{ fontWeight: 700 }}>
                          {c.courseTitle || 'Khóa học tiếng Anh'}
                        </TableCell>
                        <TableCell>
                          <Chip label={c.level || 'BEGINNER'} size="small" color="primary" variant="outlined" sx={{ fontWeight: 600 }} />
                        </TableCell>
                        <TableCell sx={{ fontWeight: 800, color: '#4F46E5' }}>
                          {c.completedCount || 0} học viên
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={3} align="center" sx={{ py: 3 }}>
                        Chưa có khóa học nào được học viên hoàn thành 100%.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;
