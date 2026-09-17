import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TextField,
  InputAdornment,
  Chip,
  LinearProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Card,
  CardContent,
  Avatar,
  Stack,
} from '@mui/material';
import {
  Search as SearchIcon,
  AssignmentTurnedIn as EnrollmentIcon,
  CheckCircle as CompleteIcon,
  HourglassEmpty as InProgressIcon,
  School as CourseIcon,
} from '@mui/icons-material';
import api from '../../services/api';

const AdminEnrollments = () => {
  const [enrollments, setEnrollments] = useState([]);
  const [courses, setCourses] = useState([]);
  const [selectedCourseFilter, setSelectedCourseFilter] = useState('ALL');
  const [stats, setStats] = useState({
    totalEnrollments: 0,
    inProgressCount: 0,
    completedCount: 0,
    notStartedCount: 0,
    completionRate: 0.0
  });
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    fetchCourses();
    fetchEnrollmentsData();
  }, [statusFilter]);

  const fetchCourses = async () => {
    try {
      const res = await api.get('/api/v1/courses');
      setCourses(res.data?.data || []);
    } catch (err) {
      console.warn('Lỗi nạp danh sách khóa học:', err);
    }
  };

  const fetchEnrollmentsData = async () => {
    setLoading(true);
    try {
      const statsRes = await api.get('/api/v1/admin/enrollments/statistics').catch(() => ({ data: { data: null } }));
      if (statsRes.data?.data) {
        setStats(statsRes.data.data);
      }

      const params = {};
      if (statusFilter !== 'ALL') params.status = statusFilter;
      if (searchTerm) params.search = searchTerm;

      const res = await api.get('/api/v1/admin/enrollments', { params });
      setEnrollments(res.data?.data || []);
    } catch (err) {
      console.error('Lỗi nạp danh sách đăng ký admin:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    if (e.key === 'Enter') {
      fetchEnrollmentsData();
    }
  };

  const filtered = enrollments.filter((e) => {
    const student = (e.studentName || '') + ' ' + (e.studentEmail || '');
    const courseTitle = e.courseName || e.course?.title || '';
    const matchesSearch = student.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          courseTitle.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesCourse = selectedCourseFilter === 'ALL' || e.courseId === selectedCourseFilter || (e.course && e.course.id === selectedCourseFilter);
    return matchesSearch && matchesCourse;
  });

  const getStatusChip = (status, completed, progress) => {
    if (completed || status === 'COMPLETED' || progress >= 100) {
      return <Chip label="Hoàn thành" size="small" color="success" sx={{ fontWeight: 700 }} />;
    }
    if (status === 'IN_PROGRESS' || (progress > 0 && progress < 100)) {
      return <Chip label="Đang học" size="small" color="primary" sx={{ fontWeight: 700 }} />;
    }
    return <Chip label="Chưa bắt đầu" size="small" color="default" sx={{ fontWeight: 700 }} />;
  };

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Quản Lý Đăng Ký Khóa Học (Enrollments)
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Theo dõi tiến độ học tập thực tế và danh sách lượt đăng ký của tất cả học viên lưu trữ trong PostgreSQL
          </Typography>
        </Box>
      </Box>

      {/* KPI Stats */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={3}>
          <Card elevation={0} sx={{ border: '1px solid #e2e8f0', borderRadius: 3, bgcolor: 'white' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ bgcolor: '#EEF2FF', color: '#4F46E5', width: 48, height: 48 }}>
                <EnrollmentIcon />
              </Avatar>
              <Box>
                <Typography variant="h5" fontWeight="bold">
                  {stats.totalEnrollments || enrollments.length}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Tổng lượt đăng ký
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card elevation={0} sx={{ border: '1px solid #e2e8f0', borderRadius: 3, bgcolor: 'white' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ bgcolor: '#FEF3C7', color: '#F59E0B', width: 48, height: 48 }}>
                <InProgressIcon />
              </Avatar>
              <Box>
                <Typography variant="h5" fontWeight="bold">
                  {stats.inProgressCount || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Đang học dở
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card elevation={0} sx={{ border: '1px solid #e2e8f0', borderRadius: 3, bgcolor: 'white' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ bgcolor: '#ECFDF5', color: '#10B981', width: 48, height: 48 }}>
                <CompleteIcon />
              </Avatar>
              <Box>
                <Typography variant="h5" fontWeight="bold">
                  {stats.completedCount || 0}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Hoàn thành khóa học
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card elevation={0} sx={{ border: '1px solid #e2e8f0', borderRadius: 3, bgcolor: 'white' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ bgcolor: '#F3F4F6', color: '#6B7280', width: 48, height: 48 }}>
                <CourseIcon />
              </Avatar>
              <Box>
                <Typography variant="h5" fontWeight="bold">
                  {stats.completionRate || 0}%
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Tỷ lệ hoàn thành
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Search & Filter Bar */}
      <Paper elevation={0} sx={{ p: 2.5, mb: 3, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={5}>
            <TextField
              fullWidth
              size="small"
              placeholder="Tìm theo tên học viên, email hoặc tên khóa học... (Bấm Enter)"
              value={searchTerm}
              onChange={handleSearchChange}
              onKeyDown={handleSearchSubmit}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={6} sm={3} md={4}>
            <FormControl fullWidth size="small">
              <InputLabel>Khóa Học (Course)</InputLabel>
              <Select value={selectedCourseFilter} label="Khóa Học (Course)" onChange={(e) => setSelectedCourseFilter(e.target.value)}>
                <MenuItem value="ALL">Tất cả khóa học</MenuItem>
                {courses.map((c) => (
                  <MenuItem key={c.id} value={c.id}>
                    {c.title}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6} sm={3} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Trạng Thái Tiến Độ</InputLabel>
              <Select value={statusFilter} label="Trạng Thái Tiến Độ" onChange={(e) => setStatusFilter(e.target.value)}>
                <MenuItem value="ALL">Tất cả trạng thái</MenuItem>
                <MenuItem value="IN_PROGRESS">Đang học</MenuItem>
                <MenuItem value="COMPLETED">Đã hoàn thành</MenuItem>
                <MenuItem value="NOT_STARTED">Chưa bắt đầu</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {loading && <LinearProgress sx={{ mb: 2, borderRadius: 1 }} />}

      {/* Table */}
      <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Table>
          <TableHead sx={{ bgcolor: '#f8fafc' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Học Viên</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Khóa Học Đã Đăng Ký</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Trình Độ</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Tiến Độ Học Tập (%)</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Trạng Thái</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Ngày Đăng Ký</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">Chưa tìm thấy bản ghi đăng ký khóa học nào trong PostgreSQL DB.</Typography>
                </TableCell>
              </TableRow>
            ) : (
              filtered
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row) => (
                  <TableRow key={row.id} hover>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        {row.studentName || (row.studentEmail ? row.studentEmail.split('@')[0] : 'Học viên')}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {row.studentEmail || 'student@gmail.com'}
                      </Typography>
                    </TableCell>

                    <TableCell sx={{ fontWeight: 700 }}>
                      {row.courseName || row.course?.title || 'Khóa học tiếng Anh'}
                    </TableCell>

                    <TableCell>
                      <Chip label={row.level || row.course?.level || 'BEGINNER'} size="small" color="primary" variant="outlined" sx={{ fontWeight: 600 }} />
                    </TableCell>

                    <TableCell sx={{ width: 220 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Box sx={{ width: '100%', mr: 1 }}>
                          <LinearProgress
                            variant="determinate"
                            value={row.progress || 0}
                            sx={{
                              height: 8,
                              borderRadius: 4,
                              bgcolor: '#e2e8f0',
                              '& .MuiLinearProgress-bar': {
                                borderRadius: 4,
                                bgcolor: (row.progress || 0) >= 100 ? '#10B981' : '#4F46E5'
                              }
                            }}
                          />
                        </Box>
                        <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700, minWidth: 45 }}>
                          {row.progress || 0}% ({row.completedLessons || 0}/{row.totalLessons ?? 0})
                        </Typography>
                      </Box>
                    </TableCell>

                    <TableCell>
                      {getStatusChip(row.status, row.completed, row.progress)}
                    </TableCell>

                    <TableCell sx={{ fontSize: '0.85rem' }}>
                      {row.enrolledAt ? new Date(row.enrolledAt).toLocaleDateString('vi-VN') : '—'}
                    </TableCell>
                  </TableRow>
                ))
            )}
          </TableBody>
        </Table>
        <TablePagination
          component="div"
          count={filtered.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => {
            setRowsPerPage(parseInt(e.target.value, 10));
            setPage(0);
          }}
          labelRowsPerPage="Số lượt đăng ký mỗi trang:"
        />
      </TableContainer>
    </Box>
  );
};

export default AdminEnrollments;
