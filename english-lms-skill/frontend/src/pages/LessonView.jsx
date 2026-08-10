import React, { useState, useEffect } from 'react';
import { Container, Grid, Box, Typography, Button, Paper, Divider, Chip, Alert, Card, CardContent } from '@mui/material';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import {
  ArrowBack as BackIcon,
  CheckCircle as CheckIcon,
  SmartToy as AiIcon,
  NavigateNext as NextIcon,
  EmojiEvents as TrophyIcon,
  Lock as LockIcon,
  ShoppingCart as CartIcon,
  PictureAsPdf as PdfIcon,
  PlayCircleOutline as VideoIcon
} from '@mui/icons-material';
import ChatBox from '../components/ChatBox';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import Loading from '../components/Loading';

const LessonView = () => {
  const navigate = useNavigate();
  const { courseId, lessonId } = useParams();
  const { user } = useAuth();

  const [lesson, setLesson] = useState(null);
  const [loading, setLoading] = useState(true);
  const [completed, setCompleted] = useState(false);
  const [progressData, setProgressData] = useState(null);
  const [msg, setMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [nextLesson, setNextLesson] = useState(null);
  const [errorState, setErrorState] = useState(null); // { type: '401' | '403' | '404' | '500', message: string }

  useEffect(() => {
    fetchLessonAndProgress();
  }, [courseId, lessonId, user]);

  const getYouTubeEmbedUrl = (url) => {
    if (!url) return null;
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return (match && match[2].length === 11) ? `https://www.youtube.com/embed/${match[2]}` : null;
  };

  const fetchLessonAndProgress = async () => {
    if (!lessonId) return;

    setLoading(true);
    setErrorState(null);
    setErrorMsg('');
    setMsg('');

    try {
      // 1. Fetch real lesson details from backend API
      const lessonRes = await api.get(`/api/v1/lessons/${lessonId}`);
      const lessonData = lessonRes.data?.data;
      setLesson(lessonData);

      // 2. Fetch course progress if logged in
      if (user && courseId) {
        try {
          const progressRes = await api.get(`/api/v1/progress/course/${courseId}`);
          const data = progressRes.data?.data;
          setProgressData(data);

          if (data?.lessons) {
            const currentLp = data.lessons.find((l) => l.lessonId === lessonId);
            if (currentLp && currentLp.completed) {
              setCompleted(true);
            }
          }

          // Fetch lessons list to find next lesson
          const lessonsListRes = await api.get(`/api/v1/courses/${courseId}/lessons`);
          const allLessons = lessonsListRes.data?.data || [];
          const currentIdx = allLessons.findIndex((l) => l.id === lessonId);
          if (currentIdx >= 0 && currentIdx < allLessons.length - 1) {
            setNextLesson(allLessons[currentIdx + 1]);
          }
        } catch (e) {
          // Progress fetch is optional
        }
      }
    } catch (err) {
      const status = err.response?.status;
      const errorCode = err.response?.data?.errorCode;
      const message = err.response?.data?.message;

      if (status === 401) {
        setErrorState({ type: '401', message: 'Vui lòng đăng nhập để xem nội dung bài học.' });
      } else if (status === 403 || errorCode === 'COURSE_PURCHASE_REQUIRED') {
        setErrorState({ type: '403', message: message || 'Khóa học yêu cầu thanh toán để học từ bài 6 trở đi. Vui lòng mua khóa học.' });
      } else if (status === 404) {
        setErrorState({ type: '404', message: 'Không tìm thấy bài học yêu cầu.' });
      } else {
        setErrorState({ type: '500', message: message || 'Đã có lỗi xảy ra từ máy chủ hệ thống.' });
      }
    } finally {
      setLoading(false);
    }
  };

  const handleMarkComplete = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (completed) return;

    setErrorMsg('');
    try {
      const res = await api.post('/api/v1/progress/complete', {
        courseId: courseId,
        lessonId: lessonId
      });
      const updatedProgress = res.data?.data;
      setCompleted(true);
      setProgressData(updatedProgress);
      setMsg('🎉 Chúc mừng! Bạn đã hoàn thành bài học này.');
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Có lỗi xảy ra khi cập nhật tiến độ bài học. Vui lòng thử lại.');
      // Keep completed as false - DO NOT fake success!
    }
  };

  const handleGoToNextLesson = () => {
    if (nextLesson) {
      navigate(`/courses/${courseId}/lessons/${nextLesson.id}`);
    }
  };

  if (loading) return <Loading title="Đang tải bài học..." />;

  if (errorState) {
    if (errorState.type === '403') {
      return (
        <Container maxWidth="md" sx={{ py: 8 }}>
          <Paper elevation={0} sx={{ p: 5, borderRadius: 4, border: '2px solid #EF4444', bgcolor: '#FEF2F2', textAlign: 'center' }}>
            <LockIcon sx={{ fontSize: 72, color: '#EF4444', mb: 2 }} />
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#991B1B', mb: 2 }}>
              Yêu Cầu Mua Khóa Học
            </Typography>
            <Typography variant="body1" sx={{ color: '#7F1D1D', mb: 4, fontSize: '1.1rem', lineHeight: 1.6 }}>
              {errorState.message}
            </Typography>

            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
              <Button
                variant="contained"
                color="primary"
                size="large"
                startIcon={<CartIcon />}
                onClick={() => navigate(`/checkout/${courseId}`)}
                sx={{ py: 1.5, px: 4, borderRadius: 3, fontWeight: 800, fontSize: '1.05rem' }}
              >
                Mua Khóa Học Ngay
              </Button>
              <Button
                variant="outlined"
                size="large"
                startIcon={<BackIcon />}
                onClick={() => navigate(`/courses/${courseId}`)}
                sx={{ py: 1.5, px: 3, borderRadius: 3, fontWeight: 700 }}
              >
                Xem Thông Tin Khóa Học
              </Button>
            </Box>
          </Paper>
        </Container>
      );
    }

    return (
      <Container maxWidth="md" sx={{ py: 8 }}>
        <Alert severity={errorState.type === '401' ? 'info' : 'error'} sx={{ mb: 3, borderRadius: 3, p: 2 }}>
          {errorState.message}
        </Alert>
        {errorState.type === '401' ? (
          <Button variant="contained" color="primary" onClick={() => navigate('/login')}>
            Đăng Nhập Ngay
          </Button>
        ) : (
          <Button startIcon={<BackIcon />} onClick={() => navigate(`/courses/${courseId}`)}>
            Quay Về Trang Khóa Học
          </Button>
        )}
      </Container>
    );
  }

  if (!lesson) return null;

  const embedUrl = getYouTubeEmbedUrl(lesson.videoUrl);
  const hasDocument = lesson.pdfUrl || lesson.documentUrl;
  const docUrl = lesson.pdfUrl || lesson.documentUrl;

  return (
    <Container maxWidth="xl" sx={{ py: 4 }} className="animate-fade-in">
      <Button startIcon={<BackIcon />} onClick={() => navigate(`/courses/${courseId}`)} sx={{ mb: 3, fontWeight: 700 }}>
        Quay lại trang chi tiết khóa học
      </Button>

      {msg && <Alert severity="success" sx={{ mb: 3, borderRadius: 3, fontWeight: 600 }}>{msg}</Alert>}
      {errorMsg && <Alert severity="error" sx={{ mb: 3, borderRadius: 3, fontWeight: 600 }}>{errorMsg}</Alert>}

      <Grid container spacing={4}>
        {/* LESSON CONTENT AREA */}
        <Grid item xs={12} md={7} lg={8}>
          <Paper elevation={0} sx={{ p: 4, borderRadius: 4, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 2 }}>
              {lesson.title}
            </Typography>

            <Box sx={{ display: 'flex', gap: 1.5, mb: 3, alignItems: 'center', flexWrap: 'wrap' }}>
              <Chip label="Bài học đang học" color="primary" size="small" sx={{ fontWeight: 700 }} />
              {completed && (
                <Chip
                  icon={<CheckIcon sx={{ fontSize: 16 }} />}
                  label="✓ Đã hoàn thành"
                  color="success"
                  size="small"
                  sx={{ fontWeight: 800 }}
                />
              )}
              {progressData && (
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>
                  Tiến độ khóa học: {progressData.completedLessons}/{progressData.totalLessons} bài ({progressData.progressPercent}%)
                </Typography>
              )}
            </Box>

            <Divider sx={{ mb: 3 }} />

            {/* SAFE YOUTUBE VIDEO IFRAME OR NULLABLE MEDIA NOTICE */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1.5, display: 'flex', alignItems: 'center', gap: 1 }}>
                <VideoIcon color="primary" /> Video Bài Giảng
              </Typography>
              {embedUrl ? (
                <Box sx={{ position: 'relative', pt: '56.25%', borderRadius: 3, overflow: 'hidden', border: '1px solid #e2e8f0', bgcolor: 'black' }}>
                  <iframe
                    src={embedUrl}
                    title={lesson.title}
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowFullScreen
                    style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', border: 'none' }}
                  />
                </Box>
              ) : (
                <Alert severity="info" variant="outlined" sx={{ borderRadius: 2 }}>
                  Bài học chưa có video bài giảng.
                </Alert>
              )}
            </Box>

            {/* TEXT CONTENT */}
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Nội dung bài học:</Typography>
            <Typography variant="body1" component="div" sx={{ whiteSpace: 'pre-line', lineHeight: 1.85, fontSize: '1.05rem', color: 'text.primary', mb: 4 }}>
              {lesson.content || 'Nội dung chi tiết đang được cập nhật.'}
            </Typography>

            {/* PDF / DOCUMENT ATTACHMENT */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1.5, display: 'flex', alignItems: 'center', gap: 1 }}>
                <PdfIcon color="secondary" /> Tài Liệu Đi Kèm
              </Typography>
              {hasDocument ? (
                <Button
                  component="a"
                  href={docUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  variant="outlined"
                  color="secondary"
                  startIcon={<PdfIcon />}
                  sx={{ borderRadius: 2.5, fontWeight: 700 }}
                >
                  Tải Về / Xem Tài Liệu PDF
                </Button>
              ) : (
                <Alert severity="info" variant="outlined" sx={{ borderRadius: 2 }}>
                  Chưa có tài liệu đính kèm.
                </Alert>
              )}
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* ACTION BUTTON & CONGRATULATIONS BANNER */}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Button
                variant={completed ? 'outlined' : 'contained'}
                color={completed ? 'success' : 'primary'}
                size="large"
                disabled={completed}
                startIcon={<CheckIcon />}
                onClick={handleMarkComplete}
                sx={{
                  fontWeight: 800,
                  py: 1.6,
                  px: 4,
                  fontSize: '1.05rem',
                  borderRadius: 3,
                  bgcolor: completed ? 'transparent' : '#10B981',
                  '&:hover': { bgcolor: completed ? 'transparent' : '#059669' }
                }}
              >
                {completed ? '✓ Đã hoàn thành' : '✓ Hoàn thành bài học'}
              </Button>

              {completed && (
                <Card sx={{ bgcolor: '#F0FDF4', border: '1px solid #10B981', borderRadius: 3 }}>
                  <CardContent sx={{ p: 2.5, display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <TrophyIcon sx={{ color: '#10B981', fontSize: 36 }} />
                      <Box>
                        <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#047857' }}>
                          🎉 Chúc mừng! Bạn đã hoàn thành bài học.
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          Tiến độ học tập của bạn đã được cập nhật tự động vào hệ thống.
                        </Typography>
                      </Box>
                    </Box>

                    {nextLesson && (
                      <Button
                        variant="contained"
                        color="primary"
                        endIcon={<NextIcon />}
                        onClick={handleGoToNextLesson}
                        sx={{ fontWeight: 800, borderRadius: 2.5, px: 3 }}
                      >
                        Học bài tiếp theo
                      </Button>
                    )}
                  </CardContent>
                </Card>
              )}
            </Box>
          </Paper>
        </Grid>

        {/* SIDEBAR AI ASSISTANT CHAT */}
        <Grid item xs={12} md={5} lg={4}>
          <Box sx={{ position: 'sticky', top: 90, height: 600 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
              <AiIcon color="primary" />
              <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>Hỏi đáp & Luyện tập cùng AI</Typography>
            </Box>
            <ChatBox />
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
};

export default LessonView;
