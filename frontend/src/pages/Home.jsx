import React, { useEffect, useState } from 'react';
import { Box, Container, Typography, Button, Grid, Paper, Stack, Card, CardContent, CircularProgress } from '@mui/material';
import {
  School as SchoolIcon,
  SmartToy as AiIcon,
  PlayArrow as PlayIcon,
  AutoAwesome as SparkleIcon,
  TrendingUp as TrendingIcon,
  Translate as LanguageIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import CourseCard from '../components/CourseCard';
import api from '../services/api';

const Home = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const res = await api.get('/api/v1/courses');
        setCourses(res.data?.data?.slice(0, 3) || []);
      } catch (err) {
        console.error('Failed to load featured courses for homepage:', err);
        setCourses([]);
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  return (
    <Box className="animate-fade-in" sx={{ pb: 8 }}>
      {/* HERO BANNER */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #3730A3 0%, #4F46E5 50%, #10B981 100%)',
          color: 'white',
          py: { xs: 8, md: 12 },
          borderRadius: { xs: 0, md: '0 0 32px 32px' },
          boxShadow: '0 20px 40px rgba(79, 70, 229, 0.2)',
          mb: 8
        }}
      >
        <Container maxWidth="lg">
          <Grid container spacing={4} alignItems="center">
            <Grid item xs={12} md={7}>
              <Box sx={{ display: 'inline-flex', alignItems: 'center', gap: 1, px: 2, py: 0.8, bgcolor: 'rgba(255,255,255,0.15)', backdropFilter: 'blur(8px)', borderRadius: 5, mb: 3 }}>
                <SparkleIcon fontSize="small" sx={{ color: '#F59E0B' }} />
                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Hệ thống Học Tiếng Anh Tích Hợp AI</Typography>
              </Box>
              <Typography variant="h2" sx={{ fontWeight: 800, fontSize: { xs: '2.2rem', md: '3.5rem' }, lineHeight: 1.2, mb: 3 }}>
                Chinh Phục Tiếng Anh Thông Minh Cùng Trợ Lý AI
              </Typography>
              <Typography variant="h6" sx={{ opacity: 0.9, fontWeight: 400, mb: 4, lineHeight: 1.6 }}>
                Học qua video bài giảng chất lượng cao, kiểm tra sửa lỗi ngữ pháp tức thì và thực hành trắc nghiệm tự động sinh bởi AI Google Gemini.
              </Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <Button
                  variant="contained"
                  color="secondary"
                  size="large"
                  startIcon={<PlayIcon />}
                  onClick={() => navigate('/courses')}
                  sx={{ py: 1.5, px: 4, fontWeight: 700, borderRadius: 3, boxShadow: '0 10px 25px rgba(16, 185, 129, 0.4)' }}
                >
                  Khám phá khóa học
                </Button>
                <Button
                  variant="outlined"
                  size="large"
                  startIcon={<AiIcon />}
                  onClick={() => navigate('/ai-assistant')}
                  sx={{
                    py: 1.5,
                    px: 3,
                    fontWeight: 600,
                    borderRadius: 3,
                    color: 'white',
                    borderColor: 'rgba(255,255,255,0.4)',
                    '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' }
                  }}
                >
                  Luyện tập với AI
                </Button>
              </Stack>
            </Grid>
            <Grid item xs={12} md={5} sx={{ display: { xs: 'none', md: 'block' } }}>
              <Box
                component="img"
                src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=800&q=80"
                alt="English LMS Students"
                sx={{
                  width: '100%',
                  borderRadius: 6,
                  boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.4)',
                  border: '4px solid rgba(255,255,255,0.2)',
                  transform: 'perspective(1000px) rotateY(-5deg)'
                }}
              />
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* FEATURE HIGHLIGHTS */}
      <Container maxWidth="lg" sx={{ mb: 8 }}>
        <Typography variant="h4" align="center" sx={{ fontWeight: 800, mb: 1 }}>
          Tại sao lựa chọn English LMS?
        </Typography>
        <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 6 }}>
          Kiến trúc Microservice hiện đại kết hợp sức mạnh Trí tuệ nhân tạo
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', textAlign: 'center', p: 3 }}>
              <Box sx={{ width: 64, height: 64, bgcolor: 'rgba(79, 70, 229, 0.1)', color: 'primary.main', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', mx: 'auto', mb: 2 }}>
                <LanguageIcon fontSize="large" />
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>Khóa Học Đa Dạng</Typography>
              <Typography variant="body2" color="text.secondary">
                Lộ trình thiết kế chuẩn khung tham chiếu Châu Âu (CEFR) từ Giao tiếp cơ bản đến IELTS & Business English.
              </Typography>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', textAlign: 'center', p: 3 }}>
              <Box sx={{ width: 64, height: 64, bgcolor: 'rgba(16, 185, 129, 0.1)', color: 'secondary.main', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', mx: 'auto', mb: 2 }}>
                <AiIcon fontSize="large" />
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>Sửa Lỗi Ngữ Pháp AI</Typography>
              <Typography variant="body2" color="text.secondary">
                AI kiểm tra tức thì câu tiếng Anh của bạn, chỉ ra lỗi sai và giải thích chi tiết bằng tiếng Việt.
              </Typography>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', textAlign: 'center', p: 3 }}>
              <Box sx={{ width: 64, height: 64, bgcolor: 'rgba(245, 158, 11, 0.1)', color: 'warning.main', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', mx: 'auto', mb: 2 }}>
                <TrendingIcon fontSize="large" />
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>Sinh Trắc Nghiệm Tự Động</Typography>
              <Typography variant="body2" color="text.secondary">
                Tự động tạo câu hỏi kiểm tra đánh giá theo bài học với lời giải chi tiết giúp bạn ôn tập hiệu quả.
              </Typography>
            </Card>
          </Grid>
        </Grid>
      </Container>

      {/* FEATURED COURSES PREVIEW */}
      <Container maxWidth="lg">
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>Khóa học nổi bật</Typography>
            <Typography variant="body1" color="text.secondary">Bắt đầu học ngay hôm nay với các khóa học chất lượng cao</Typography>
          </Box>
          <Button variant="outlined" color="primary" onClick={() => navigate('/courses')}>
            Xem tất cả
          </Button>
        </Box>

        {loading ? (
          <Box sx={{ py: 6, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress color="primary" />
          </Box>
        ) : courses.length > 0 ? (
          <Grid container spacing={3}>
            {courses.map((course) => (
              <Grid item key={course.id} xs={12} sm={6} md={4}>
                <CourseCard course={course} />
              </Grid>
            ))}
          </Grid>
        ) : (
          <Box sx={{ py: 6, textAlign: 'center' }}>
            <Typography variant="body1" color="text.secondary">
              Chưa có khóa học nổi bật nào. Nhấn "Xem tất cả" để khám phá thư viện khóa học.
            </Typography>
          </Box>
        )}
      </Container>
    </Box>
  );
};

export default Home;
