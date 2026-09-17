import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Box,
  Typography,
  Card,
  CardContent,
  CardMedia,
  Button,
  LinearProgress,
  Chip,
  TextField,
  InputAdornment,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Paper
} from '@mui/material';
import {
  Search as SearchIcon,
  PlayCircle as PlayIcon,
  CheckCircle as CompleteIcon,
  HourglassTop as InProgressIcon,
  School as CourseIcon,
  Bookmark as BookmarkIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import Loading from '../components/Loading';

const MyCourses = () => {
  const navigate = useNavigate();
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    const fetchMyCourses = async () => {
      try {
        const res = await api.get('/api/v1/enrollments/me');
        setEnrollments(res.data?.data || []);
      } catch (err) {
        setEnrollments([]);
      } finally {
        setLoading(false);
      }
    };
    fetchMyCourses();
  }, []);

  const getStatusBadge = (status, completed, progress) => {
    if (completed || status === 'COMPLETED' || progress >= 100) {
      return <Chip icon={<CompleteIcon sx={{ fontSize: 16 }} />} label="Đã hoàn thành" color="success" size="small" sx={{ fontWeight: 700 }} />;
    }
    if (status === 'IN_PROGRESS' || (progress > 0 && progress < 100)) {
      return <Chip icon={<InProgressIcon sx={{ fontSize: 16 }} />} label="Đang học" color="primary" size="small" sx={{ fontWeight: 700 }} />;
    }
    return <Chip label="Chưa bắt đầu" color="default" size="small" sx={{ fontWeight: 700 }} />;
  };

  const filteredEnrollments = enrollments.filter((item) => {
    const title = item.courseName || item.course?.title || '';
    const matchesSearch = title.toLowerCase().includes(search.toLowerCase());

    if (statusFilter === 'ALL') return matchesSearch;
    if (statusFilter === 'COMPLETED') return matchesSearch && (item.completed || item.status === 'COMPLETED' || item.progress >= 100);
    if (statusFilter === 'IN_PROGRESS') return matchesSearch && (item.status === 'IN_PROGRESS' || (item.progress > 0 && item.progress < 100));
    if (statusFilter === 'NOT_STARTED') return matchesSearch && (item.status === 'NOT_STARTED' || item.progress === 0);
    return matchesSearch;
  });

  if (loading) return <Loading message="Đang nạp danh sách khóa học của bạn..." />;

  return (
    <Container maxWidth="lg" sx={{ py: 6 }} className="animate-fade-in">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" sx={{ fontWeight: 800, mb: 1, display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <BookmarkIcon color="primary" fontSize="large" /> Khóa Học Của Tôi
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Theo dõi tiến độ học tập, bài học đã hoàn thành và tiếp tục trau dồi tiếng Anh mỗi ngày
        </Typography>
      </Box>

      {/* SEARCH AND FILTER */}
      <Paper elevation={0} sx={{ p: 2.5, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={8}>
            <TextField
              fullWidth
              size="small"
              placeholder="Tìm kiếm khóa học đã đăng ký..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon color="action" />
                  </InputAdornment>
                )
              }}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small">
              <InputLabel>Trạng thái học</InputLabel>
              <Select value={statusFilter} label="Trạng thái học" onChange={(e) => setStatusFilter(e.target.value)}>
                <MenuItem value="ALL">Tất cả trạng thái</MenuItem>
                <MenuItem value="IN_PROGRESS">Đang học</MenuItem>
                <MenuItem value="COMPLETED">Đã hoàn thành</MenuItem>
                <MenuItem value="NOT_STARTED">Chưa bắt đầu</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {/* ENROLLED COURSES LIST */}
      {filteredEnrollments.length > 0 ? (
        <Grid container spacing={3}>
          {filteredEnrollments.map((item) => {
            const courseId = item.courseId || item.course?.id;
            const courseTitle = item.courseName || item.course?.title || 'Khóa học Tiếng Anh';
            const thumbnail = item.thumbnail || item.course?.imageUrl || 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80';
            const level = item.level || item.course?.level || 'Cơ bản';
            const progress = item.progress || 0;
            const completedLessons = item.completedLessons || 0;
            const totalLessons = item.totalLessons ?? 0;
            const enrolledDate = item.enrolledAt ? new Date(item.enrolledAt).toLocaleDateString('vi-VN') : 'Mới đăng ký';

            return (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <Card
                  elevation={0}
                  sx={{
                    borderRadius: 4,
                    border: '1px solid #e2e8f0',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    transition: 'all 0.3s ease',
                    '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 12px 24px -10px rgba(0,0,0,0.1)' }
                  }}
                >
                  <CardMedia component="img" height="160" image={thumbnail} alt={courseTitle} />
                  <CardContent sx={{ p: 2.5, flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                      <Chip label={level} size="small" color="primary" sx={{ fontWeight: 700 }} />
                      {getStatusBadge(item.status, item.completed, progress)}
                    </Box>

                    <Typography variant="h6" sx={{ fontWeight: 800, mb: 1, lineHeight: 1.3, height: 48, overflow: 'hidden' }}>
                      {courseTitle}
                    </Typography>

                    <Box sx={{ mt: 'auto', pt: 2 }}>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.8 }}>
                        <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                          Tiến độ: {completedLessons}/{totalLessons} bài ({progress}%)
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {enrolledDate}
                        </Typography>
                      </Box>

                      <LinearProgress
                        variant="determinate"
                        value={progress}
                        sx={{
                          height: 8,
                          borderRadius: 4,
                          bgcolor: '#e2e8f0',
                          '& .MuiLinearProgress-bar': {
                            borderRadius: 4,
                            bgcolor: progress >= 100 ? '#10B981' : '#4F46E5'
                          }
                        }}
                      />

                      <Button
                        fullWidth
                        variant="contained"
                        startIcon={<PlayIcon />}
                        onClick={() => navigate(`/courses/${courseId}`)}
                        sx={{
                          mt: 2.5,
                          borderRadius: 2.5,
                          fontWeight: 800,
                          py: 1,
                          bgcolor: progress >= 100 ? '#10B981' : '#4F46E5',
                          '&:hover': { bgcolor: progress >= 100 ? '#059669' : '#4338CA' }
                        }}
                      >
                        {progress >= 100 ? 'Xem lại bài học' : 'Tiếp tục học'}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      ) : (
        <Paper elevation={0} sx={{ p: 6, textCenter: 'center', textAlign: 'center', borderRadius: 4, border: '1px solid #e2e8f0' }}>
          <CourseIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2, opacity: 0.5 }} />
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
            Chưa tìm thấy khóa học nào
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            {search || statusFilter !== 'ALL'
              ? 'Không có khóa học nào khớp với bộ lọc tìm kiếm của bạn.'
              : 'Bạn chưa đăng ký khóa học tiếng Anh nào. Hãy khám phá danh sách khóa học phong phú của English LMS!'}
          </Typography>
          <Button variant="contained" color="primary" onClick={() => navigate('/courses')} sx={{ fontWeight: 800, px: 3, py: 1 }}>
            Khám phá Khóa học ngay
          </Button>
        </Paper>
      )}
    </Container>
  );
};

export default MyCourses;
