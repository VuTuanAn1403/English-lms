import React, { useState } from 'react';
import { Container, Grid, Box, Typography, Button, Paper, Divider, Chip } from '@mui/material';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { ArrowBack as BackIcon, CheckCircle as CheckIcon, SmartToy as AiIcon } from '@mui/icons-material';
import ChatBox from '../components/ChatBox';

const LessonView = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const { courseId } = useParams();

  const lesson = state?.lesson || {
    id: 'l1',
    title: 'Bài 1: Present Simple - Thì Hiện Tại Đơn',
    content: '### Thì hiện tại đơn (Present Simple)\n1. **Khái niệm**: Diễn tả hành động lặp đi lặp lại hoặc sự thật hiển nhiên.\n2. **Công thức**:\n- (+) S + V(s/es)\n- (-) S + do/does + not + V_inf\n- (?) Do/Does + S + V_inf?\n3. **Ví dụ**: She *works* at a hospital.',
    videoUrl: 'https://www.youtube.com/embed/dQw4w9WgXcQ'
  };

  const [completed, setCompleted] = useState(false);

  return (
    <Container maxWidth="xl" sx={{ py: 4 }} className="animate-fade-in">
      <Button startIcon={<BackIcon />} onClick={() => navigate(`/courses/${courseId}`)} sx={{ mb: 3 }}>
        Quay lại trang chi tiết khóa học
      </Button>

      <Grid container spacing={4}>
        {/* LESSON CONTENT AREA */}
        <Grid item xs={12} md={7} lg={8}>
          <Paper sx={{ p: 4, borderRadius: 4 }}>
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 2 }}>
              {lesson.title}
            </Typography>

            <Box sx={{ display: 'flex', gap: 1, mb: 3 }}>
              <Chip label="Đang học" color="primary" size="small" sx={{ fontWeight: 700 }} />
              {completed && <Chip icon={<CheckIcon />} label="Đã hoàn thành" color="success" size="small" sx={{ fontWeight: 700 }} />}
            </Box>

            <Divider sx={{ mb: 3 }} />

            {/* VIDEO PLAYER PLACEHOLDER / IFRAME */}
            {lesson.videoUrl && (
              <Box sx={{ width: '100%', height: 360, bgcolor: 'black', borderRadius: 3, mb: 3, overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Typography color="white" variant="subtitle1" sx={{ fontWeight: 600 }}>
                  [Video bài giảng: {lesson.title}]
                </Typography>
              </Box>
            )}

            {/* TEXT CONTENT */}
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Nội dung bài học:</Typography>
            <Typography variant="body1" component="div" sx={{ whiteSpace: 'pre-line', lineHeight: 1.8, fontSize: '1.05rem', color: 'text.primary', mb: 4 }}>
              {lesson.content}
            </Typography>

            <Button
              variant={completed ? 'outlined' : 'contained'}
              color={completed ? 'success' : 'primary'}
              size="large"
              startIcon={<CheckIcon />}
              onClick={() => setCompleted(!completed)}
              sx={{ fontWeight: 700 }}
            >
              {completed ? 'Đã đánh dấu hoàn thành' : 'Đánh dấu hoàn thành bài học'}
            </Button>
          </Paper>
        </Grid>

        {/* SIDEBAR AI ASSISTANT CHAT */}
        <Grid item xs={12} md={5} lg={4}>
          <Box sx={{ position: 'sticky', top: 90 }}>
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
