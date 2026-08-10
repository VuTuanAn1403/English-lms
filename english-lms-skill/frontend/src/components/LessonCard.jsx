import React from 'react';
import { Card, CardContent, Typography, Box, IconButton, Chip } from '@mui/material';
import {
  PlayCircleOutline as PlayIcon,
  PictureAsPdf as PdfIcon,
  ChevronRight as ArrowIcon,
  Lock as LockIcon,
  CheckCircle as CheckIcon,
  School as TrialIcon
} from '@mui/icons-material';

const LessonCard = ({ lesson, index, isLocked, isCompleted, onSelect }) => {
  const isTrialLesson = index < 5;

  return (
    <Card
      onClick={() => onSelect(lesson)}
      sx={{
        mb: 1.5,
        cursor: 'pointer',
        border: '1px solid',
        borderColor: isLocked ? '#FCA5A5' : isCompleted ? '#6EE7B7' : '#E2E8F0',
        bgcolor: isLocked ? '#FEF2F2' : isCompleted ? '#F0FDF4' : 'white',
        transition: 'all 0.2s ease',
        '&:hover': {
          borderColor: isLocked ? '#EF4444' : 'primary.main',
          bgcolor: isLocked ? '#FEE2E2' : 'rgba(79, 70, 229, 0.04)',
          transform: 'translateX(4px)',
        },
      }}
    >
      <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Box
            sx={{
              width: 38,
              height: 38,
              borderRadius: '50%',
              bgcolor: isLocked ? '#EF4444' : isCompleted ? '#10B981' : 'primary.main',
              color: 'white',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '0.9rem',
              flexShrink: 0,
            }}
          >
            {isLocked ? <LockIcon fontSize="small" /> : isCompleted ? <CheckIcon fontSize="small" /> : index + 1}
          </Box>

          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 700, color: isLocked ? '#991B1B' : 'text.primary' }}>
                {lesson.title}
              </Typography>
              {isTrialLesson && (
                <Chip icon={<TrialIcon sx={{ fontSize: 14 }} />} label="Học Thử" size="small" color="primary" sx={{ height: 20, fontSize: '0.72rem', fontWeight: 800 }} />
              )}
              {isLocked && (
                <Chip icon={<LockIcon sx={{ fontSize: 14 }} />} label="Yêu cầu mua" size="small" color="error" sx={{ height: 20, fontSize: '0.72rem', fontWeight: 800 }} />
              )}
            </Box>
            <Typography variant="body2" color={isLocked ? '#B91C1C' : 'text.secondary'} noWrap sx={{ maxWidth: 400, fontSize: '0.85rem' }}>
              {isLocked ? 'Cần mua khóa học để mở khóa bài học này.' : (lesson.content ? lesson.content.replace(/<[^>]*>?/gm, '') : 'Nội dung bài học chuẩn kiến thức tiếng Anh.')}
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {lesson.videoUrl && !isLocked && <Chip icon={<PlayIcon fontSize="small" />} label="Video" size="small" color="primary" variant="outlined" />}
            {lesson.pdfUrl && !isLocked && <Chip icon={<PdfIcon fontSize="small" />} label="Tài liệu" size="small" color="secondary" variant="outlined" />}
            <IconButton size="small" color={isLocked ? 'error' : 'primary'}>
              {isLocked ? <LockIcon fontSize="small" /> : <ArrowIcon />}
            </IconButton>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default LessonCard;
