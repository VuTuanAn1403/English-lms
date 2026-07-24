import React, { useState, useEffect } from 'react';
import { Container, Grid, Box, Typography, Button, Paper, Chip, Divider, Alert } from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { PlayCircle as PlayIcon, CheckCircle as CheckIcon, SmartToy as AiIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import LessonCard from '../components/LessonCard';
import Loading from '../components/Loading';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const sampleLessons = [
  { id: 'l1', title: 'Bài 1: Present Simple - Thì Hiện Tại Đơn', content: 'Cấu trúc, cách dùng và các dấu hiệu nhận biết thì hiện tại đơn trong tiếng Anh.', videoUrl: 'https://youtube.com', pdfUrl: 'https://pdf.com' },
  { id: 'l2', title: 'Bài 2: Present Continuous - Thì Hiện Tại Tiếp Diễn', content: 'Diễn tả hành động đang diễn ra tại thời điểm nói hoặc kế hoạch tương lai gần.', videoUrl: 'https://youtube.com', pdfUrl: null },
  { id: 'l3', title: 'Bài 3: Past Simple - Thì Quá Khứ Đơn', content: 'Sử dụng động từ có quy tắc và bất quy tắc để kể lại sự việc trong quá khứ.', videoUrl: null, pdfUrl: 'https://pdf.com' },
  { id: 'l4', title: 'Bài 4: Future Simple - Thì Tương Lai Đơn', content: 'Diễn tả quyết định tức thì, lời hứa hoặc dự đoán không có căn cứ.', videoUrl: 'https://youtube.com', pdfUrl: 'https://pdf.com' }
];

const CourseDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState(null);
  const [lessons, setLessons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [enrolled, setEnrolled] = useState(false);
  const [msg, setMsg] = useState('');

  useEffect(() => {
    const fetchCourseDetail = async () => {
      try {
        const res = await api.get(`/api/v1/courses/${id}`);
        setCourse(res.data?.data || null);
        const lessonRes = await api.get(`/api/v1/courses/${id}/lessons`);
        setLessons(lessonRes.data?.data || sampleLessons);
      } catch (err) {
        setCourse({
          id: id,
          title: 'English Communication - Giao Tiếp Căn Bản',
          description: 'Khóa học thiết kế chuẩn quốc tế giúp bạn làm chủ kỹ năng giao tiếp tiếng Anh, bổ sung từ vựng và luyện tập trắc nghiệm tự động cùng Trợ lý AI.',
          level: 'Cơ bản',
          imageUrl: 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80'
        });
        setLessons(sampleLessons);
      } finally {
        setLoading(false);
      }
    };
    fetchCourseDetail();
  }, [id]);

  const handleEnroll = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    try {
      await api.post('/api/v1/enrollments', { courseId: id });
      setEnrolled(true);
      setMsg('Đăng ký khóa học thành công! Bạn có thể bắt đầu học ngay.');
    } catch (err) {
      setEnrolled(true);
      setMsg('Bạn đã đăng ký khóa học này!');
    }
  };

  const handleSelectLesson = (lesson) => {
    navigate(`/courses/${id}/lessons/${lesson.id}`, { state: { lesson, lessons } });
  };

  if (loading) return <Loading message="Đang tải thông tin khóa học..." />;
  if (!course) return <Container sx={{ py: 6 }}><Typography>Không tìm thấy khóa học.</Typography></Container>;

  return (
    <Container maxWidth="lg" sx={{ py: 6 }} className="animate-fade-in">
      <Button startIcon={<BackIcon />} onClick={() => navigate('/courses')} sx={{ mb: 3 }}>
        Quay lại danh sách khóa học
      </Button>

      {msg && <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>{msg}</Alert>}

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Box sx={{ mb: 4 }}>
            <Chip label={course.level || 'Cơ bản'} color="primary" sx={{ fontWeight: 700, mb: 2 }} />
            <Typography variant="h3" sx={{ fontWeight: 800, mb: 2, lineHeight: 1.2 }}>
              {course.title}
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ fontSize: '1.1rem', lineHeight: 1.7, mb: 4 }}>
              {course.description}
            </Typography>
          </Box>

          <Divider sx={{ my: 4 }} />

          <Typography variant="h5" sx={{ fontWeight: 700, mb: 3 }}>
            Danh sách Bài học ({lessons.length} bài)
          </Typography>

          {lessons.map((lesson, idx) => (
            <LessonCard key={lesson.id || idx} lesson={lesson} index={idx} onSelect={handleSelectLesson} />
          ))}
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper className="glass-card" sx={{ p: 3, borderRadius: 4, position: 'sticky', top: 90 }}>
            <Box
              component="img"
              src={course.imageUrl}
              alt={course.title}
              sx={{ width: '100%', height: 200, objectFit: 'cover', borderRadius: 3, mb: 3 }}
            />

            <Button
              fullWidth
              variant="contained"
              color={enrolled ? 'secondary' : 'primary'}
              size="large"
              startIcon={enrolled ? <CheckIcon /> : <PlayIcon />}
              onClick={handleEnroll}
              sx={{ py: 1.5, fontWeight: 800, mb: 2 }}
            >
              {enrolled ? 'Đã đăng ký - Vào học ngay' : 'Đăng ký khóa học ngay'}
            </Button>

            <Button
              fullWidth
              variant="outlined"
              color="primary"
              startIcon={<AiIcon />}
              onClick={() => navigate('/ai-assistant')}
              sx={{ py: 1.2, fontWeight: 700 }}
            >
              Trợ lý AI hỗ trợ khóa học
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default CourseDetail;
