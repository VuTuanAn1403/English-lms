import React from 'react';
import { Card, CardContent, Typography, Box, IconButton, Chip } from '@mui/material';
import { PlayCircleOutline as PlayIcon, PictureAsPdf as PdfIcon, ChevronRight as ArrowIcon } from '@mui/icons-material';

const LessonCard = ({ lesson, index, onSelect }) => {
  return (
    <Card
      onClick={() => onSelect(lesson)}
      sx={{
        mb: 2,
        cursor: 'pointer',
        border: '1px solid #E2E8F0',
        transition: 'all 0.2s ease',
        '&:hover': {
          borderColor: 'primary.main',
          bgcolor: 'rgba(79, 70, 229, 0.02)',
          transform: 'translateX(4px)',
        },
      }}
    >
      <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Box
            sx={{
              width: 40,
              height: 40,
              borderRadius: '50%',
              bgcolor: 'primary.main',
              color: 'white',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 700,
              fontSize: '0.9rem',
              flexShrink: 0,
            }}
          >
            {index + 1}
          </Box>

          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, color: 'text.primary' }}>
              {lesson.title}
            </Typography>
            <Typography variant="body2" color="text.secondary" noWrap sx={{ maxWidth: 400 }}>
              {lesson.content ? lesson.content.replace(/<[^>]*>?/gm, '') : 'Nội dung bài học chuẩn kiến thức tiếng Anh.'}
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {lesson.videoUrl && <Chip icon={<PlayIcon fontSize="small" />} label="Video" size="small" color="primary" variant="outlined" />}
            {lesson.pdfUrl && <Chip icon={<PdfIcon fontSize="small" />} label="Tài liệu" size="small" color="secondary" variant="outlined" />}
            <IconButton size="small" color="primary">
              <ArrowIcon />
            </IconButton>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default LessonCard;
