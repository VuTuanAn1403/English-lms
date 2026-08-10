import React, { useState, useEffect } from 'react';
import { Container, Grid, Box, Typography, Button, Paper, Chip, Divider, Alert, Stack, LinearProgress, Card, CardContent } from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import {
  PlayCircle as PlayIcon,
  CheckCircle as CheckIcon,
  SmartToy as AiIcon,
  ArrowBack as BackIcon,
  People as PeopleIcon,
  School as SchoolIcon,
  Lock as LockIcon,
  ShoppingCart as CartIcon,
  EmojiEvents as TrophyIcon,
  LocalOffer as OfferIcon
} from '@mui/icons-material';
import LessonCard from '../components/LessonCard';
import Loading from '../components/Loading';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const CourseDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState(null);
  const [lessons, setLessons] = useState([]);
  const [access, setAccess] = useState(null);
  const [loading, setLoading] = useState(true);
  const [progressInfo, setProgressInfo] = useState(null);
  const [enrolledCount, setEnrolledCount] = useState(0);
  const [msg, setMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [trialLoading, setTrialLoading] = useState(false);

  useEffect(() => {
    fetchCourseDetail();
  }, [id, user]);

  const fetchCourseDetail = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/api/v1/courses/${id}`);
      setCourse(res.data?.data || null);

      const lessonRes = await api.get(`/api/v1/courses/${id}/lessons`);
      setLessons(lessonRes.data?.data || []);

      const countRes = await api.get(`/api/v1/courses/${id}/enrolled-count`).catch(() => ({ data: { data: 0 } }));
      setEnrolledCount(countRes.data?.data || 0);

      const accessRes = await api.get(`/api/v1/courses/${id}/access`).catch(() => null);
      if (accessRes?.data?.data) {
        setAccess(accessRes.data.data);
      }

      if (user) {
        try {
          const progressRes = await api.get(`/api/v1/progress/course/${id}`);
          if (progressRes.data?.data) {
            setProgressInfo(progressRes.data.data);
          }
        } catch (e) {
          // No progress record yet
        }
      }
    } catch (err) {
      setErrorMsg('Không thể nạp thông tin khóa học.');
    } finally {
      setLoading(false);
    }
  };

  const handleStartTrial = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    setTrialLoading(true);
    setMsg('');
    setErrorMsg('');
    try {
      await api.post(`/api/v1/courses/${id}/trial`);
      setMsg('🎉 Đã kích hoạt học thử 5 bài đầu tiên miễn phí! Bạn có thể học ngay bây giờ.');
      await fetchCourseDetail();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Không thể đăng ký học thử.');
    } finally {
      setTrialLoading(false);
    }
  };

  const handleEnrollFree = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    try {
      await api.post('/api/v1/enrollments', { courseId: id });
      setMsg('🎉 Đăng ký khóa học miễn phí thành công!');
      await fetchCourseDetail();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Không thể đăng ký khóa học.');
    }
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  if (loading) return <Loading title="Đang tải thông tin khóa học..." />;

  if (!course) {
    return (
      <Container sx={{ py: 8 }}>
        <Alert severity="warning">Không tìm thấy khóa học yêu cầu.</Alert>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/courses')} sx={{ mt: 2 }}>
          Quay về danh sách khóa học
        </Button>
      </Container>
    );
  }

  const isFree = course.isFree;
  const hasFullAccess = access?.hasFullAccess || false;
  const isTrial = access?.enrollmentStatus === 'TRIAL';
  const canStartTrial = access?.canStartTrial && !isTrial && !hasFullAccess;
  const effectivePrice = course.effectivePrice || 0;
  const originalPrice = course.price || 0;
  const hasDiscount = originalPrice > effectivePrice;

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Button startIcon={<BackIcon />} onClick={() => navigate('/courses')} sx={{ mb: 3, fontWeight: 700 }}>
        Quay lại danh sách khóa học
      </Button>

      {msg && <Alert severity="success" sx={{ mb: 3, borderRadius: 3, fontWeight: 600 }}>{msg}</Alert>}
      {errorMsg && <Alert severity="error" sx={{ mb: 3, borderRadius: 3, fontWeight: 600 }}>{errorMsg}</Alert>}

      <Grid container spacing={4}>
        {/* LEFT COLUMN: HERO & LESSON LIST */}
        <Grid item xs={12} md={8}>
          <Paper elevation={0} sx={{ p: 4, borderRadius: 4, border: '1px solid #e2e8f0', bgcolor: 'white', mb: 4 }}>
            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap', alignItems: 'center' }}>
              <Chip label={course.level || 'Cơ bản'} color="primary" sx={{ fontWeight: 800 }} />
              {isFree ? (
                <Chip label="Miễn phí" color="success" sx={{ fontWeight: 800 }} />
              ) : hasFullAccess ? (
                <Chip label="Đã sở hữu" color="success" icon={<CheckIcon />} sx={{ fontWeight: 800 }} />
              ) : isTrial ? (
                <Chip label="Đang Học Thử 5 Bài" color="warning" icon={<SchoolIcon />} sx={{ fontWeight: 800 }} />
              ) : (
                <Chip label="Trả phí" color="secondary" sx={{ fontWeight: 800 }} />
              )}
            </Box>

            <Typography variant="h4" sx={{ fontWeight: 900, mb: 2, color: 'text.primary', lineHeight: 1.3 }}>
              {course.title}
            </Typography>

            <Typography variant="body1" color="text.secondary" sx={{ mb: 3, lineHeight: 1.7, fontSize: '1.05rem' }}>
              {course.description}
            </Typography>

            <Stack direction="row" spacing={3} sx={{ color: 'text.secondary', fontSize: '0.9rem', fontWeight: 600 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.8 }}>
                <SchoolIcon color="primary" fontSize="small" />
                <span>{lessons.length} Bài học</span>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.8 }}>
                <PeopleIcon color="primary" fontSize="small" />
                <span>{enrolledCount} Học viên</span>
              </Box>
            </Stack>
          </Paper>

          {/* DYNAMIC PROGRESS BAR IF ENROLLED OR TRIAL */}
          {access?.enrollmentStatus && (
            <Paper elevation={0} sx={{ p: 3, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white', mb: 4 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'primary.main', display: 'flex', alignItems: 'center', gap: 1 }}>
                  <TrophyIcon color="warning" /> Tiến Độ Học Tập
                </Typography>
                <Typography variant="body2" sx={{ fontWeight: 800 }}>
                  {progressInfo?.percentage || 0}% ({progressInfo?.completedLessons || 0}/{lessons.length} bài)
                </Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={progressInfo?.percentage || 0}
                sx={{ height: 10, borderRadius: 5, bgcolor: '#e2e8f0', '& .MuiLinearProgress-bar': { bgcolor: 'primary.main', borderRadius: 5 } }}
              />
            </Paper>
          )}

          {/* LESSONS LIST WITH FREE TRIAL & LOCK BADGES */}
          <Paper elevation={0} sx={{ p: 3.5, borderRadius: 4, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
              Danh Sách Bài Học ({lessons.length} bài)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              {!isFree && !hasFullAccess ? '5 bài học đầu tiên được miễn phí học thử. Mua khóa học để mở toàn bộ bài học từ bài 6 trở đi.' : 'Toàn bộ bài học sẵn sàng truy cập.'}
            </Typography>

            <Stack spacing={2}>
              {lessons.map((lesson, index) => {
                const isTrialLesson = index < 5;
                const isLocked = !isFree && !hasFullAccess && !isTrialLesson;
                const isCompleted = progressInfo?.completedLessonIds?.includes(lesson.id);

                return (
                  <LessonCard
                    key={lesson.id}
                    lesson={lesson}
                    index={index}
                    isLocked={isLocked}
                    isCompleted={isCompleted}
                    onSelect={() => {
                      if (isLocked) {
                        navigate(`/checkout/${id}`);
                      } else {
                        navigate(`/courses/${id}/lessons/${lesson.id}`, { state: { lesson, lessons } });
                      }
                    }}
                  />
                );
              })}
            </Stack>
          </Paper>
        </Grid>

        {/* RIGHT COLUMN: PRICING & CALL TO ACTION */}
        <Grid item xs={12} md={4}>
          <Paper elevation={0} sx={{ p: 3.5, borderRadius: 4, border: '1px solid #e2e8f0', bgcolor: 'white', position: 'sticky', top: 20 }}>
            <Box
              component="img"
              src={course.imageUrl || 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d'}
              alt={course.title}
              sx={{ width: '100%', height: 200, objectFit: 'cover', borderRadius: 3, mb: 3 }}
            />

            {!isFree && (
              <Box sx={{ mb: 3 }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700, textTransform: 'uppercase' }}>
                  HỌC PHÍ KHÓA HỌC:
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 1.5, mt: 0.5 }}>
                  <Typography variant="h4" sx={{ fontWeight: 900, color: 'primary.main' }}>
                    {formatVND(effectivePrice)}
                  </Typography>
                  {hasDiscount && (
                    <Typography variant="body1" sx={{ textDecoration: 'line-through', color: 'text.secondary', fontWeight: 600 }}>
                      {formatVND(originalPrice)}
                    </Typography>
                  )}
                </Box>
              </Box>
            )}

            <Stack spacing={2}>
              {hasFullAccess ? (
                <Button
                  fullWidth
                  variant="contained"
                  size="large"
                  color="success"
                  startIcon={<PlayIcon />}
                  onClick={() => {
                    const firstLesson = lessons[0];
                    if (firstLesson) {
                      navigate(`/courses/${id}/lessons/${firstLesson.id}`, { state: { lesson: firstLesson, lessons } });
                    }
                  }}
                  sx={{ py: 1.5, borderRadius: 2.5, fontWeight: 800, fontSize: '1rem' }}
                >
                  Vào Học Ngay
                </Button>
              ) : isFree ? (
                <Button
                  fullWidth
                  variant="contained"
                  size="large"
                  color="primary"
                  startIcon={<PlayIcon />}
                  onClick={handleEnrollFree}
                  sx={{ py: 1.5, borderRadius: 2.5, fontWeight: 800 }}
                >
                  Đăng Ký Miễn Phí
                </Button>
              ) : (
                <>
                  <Button
                    fullWidth
                    variant="contained"
                    size="large"
                    color="primary"
                    startIcon={<CartIcon />}
                    onClick={() => navigate(`/checkout/${id}`)}
                    sx={{ py: 1.5, borderRadius: 2.5, fontWeight: 900, fontSize: '1.05rem', boxShadow: '0 4px 14px rgba(79,70,229,0.3)' }}
                  >
                    Mua Ngay ({formatVND(effectivePrice)})
                  </Button>

                  {canStartTrial && (
                    <Button
                      fullWidth
                      variant="outlined"
                      size="large"
                      color="secondary"
                      startIcon={<SchoolIcon />}
                      onClick={handleStartTrial}
                      disabled={trialLoading}
                      sx={{ py: 1.2, borderRadius: 2.5, fontWeight: 800 }}
                    >
                      Học Thử 5 Bài Miễn Phí
                    </Button>
                  )}

                  {isTrial && (
                    <Button
                      fullWidth
                      variant="contained"
                      size="large"
                      color="warning"
                      startIcon={<PlayIcon />}
                      onClick={() => {
                        const firstLesson = lessons[0];
                        if (firstLesson) {
                          navigate(`/courses/${id}/lessons/${firstLesson.id}`, { state: { lesson: firstLesson, lessons } });
                        }
                      }}
                      sx={{ py: 1.2, borderRadius: 2.5, fontWeight: 800 }}
                    >
                      Tiếp Tục Học Thử (5 Bài)
                    </Button>
                  )}
                </>
              )}
            </Stack>

            <Divider sx={{ my: 3 }} />

            <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 1.5 }}>
              Quyền Lợi Học Viên:
            </Typography>
            <Stack spacing={1}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary', fontSize: '0.88rem' }}>
                <CheckIcon color="success" fontSize="small" />
                <span>Quyền truy cập học tập không giới hạn thời gian</span>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary', fontSize: '0.88rem' }}>
                <CheckIcon color="success" fontSize="small" />
                <span>Trợ lý AI Teacher giải đáp thắc mắc 24/7</span>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary', fontSize: '0.88rem' }}>
                <CheckIcon color="success" fontSize="small" />
                <span>Tự động sinh Quiz trắc nghiệm củng cố kiến thức</span>
              </Box>
            </Stack>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default CourseDetail;
