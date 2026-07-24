import React from 'react';
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  Button,
  Chip,
  Box,
  CardActions
} from '@mui/material';
import {
  Book as BookIcon,
  PlayCircle as PlayIcon,
  Star as StarIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const CourseCard = ({ course }) => {
  const navigate = useNavigate();

  const getLevelColor = (level) => {
    switch (level?.toLowerCase()) {
      case 'beginner':
      case 'cơ bản':
        return 'success';
      case 'intermediate':
      case 'trung cấp':
        return 'warning';
      case 'advanced':
      case 'nâng cao':
        return 'error';
      default:
        return 'primary';
    }
  };

  const defaultImage = 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80';

  return (
    <Card
      className="glass-card animate-fade-in"
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.3s ease, box-shadow 0.3s ease',
        '&:hover': {
          transform: 'translateY(-6px)',
          boxShadow: '0 12px 30px rgba(79, 70, 229, 0.15)',
        },
      }}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="180"
          image={course.imageUrl || defaultImage}
          alt={course.title}
          sx={{ objectFit: 'cover' }}
        />
        <Chip
          label={course.level || 'Cơ bản'}
          color={getLevelColor(course.level)}
          size="small"
          sx={{
            position: 'absolute',
            top: 12,
            right: 12,
            fontWeight: 700,
            boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
          }}
        />
      </Box>

      <CardContent sx={{ flexGrow: 1, p: 2.5 }}>
        <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 700, lineHeight: 1.3 }}>
          {course.title}
        </Typography>

        <Typography variant="body2" color="text.secondary" sx={{
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden',
          mb: 2,
          lineHeight: 1.6
        }}>
          {course.description || 'Khóa học tiếng Anh giúp nâng cao trình độ nhanh chóng.'}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary', fontSize: '0.875rem' }}>
          <BookIcon fontSize="small" color="primary" />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>Tiếng Anh ứng dụng</Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', ml: 'auto', color: '#F59E0B' }}>
            <StarIcon fontSize="small" />
            <Typography variant="caption" sx={{ fontWeight: 700, ml: 0.5, color: 'text.primary' }}>4.9</Typography>
          </Box>
        </Box>
      </CardContent>

      <CardActions sx={{ p: 2.5, pt: 0 }}>
        <Button
          fullWidth
          variant="contained"
          color="primary"
          startIcon={<PlayIcon />}
          onClick={() => navigate(`/courses/${course.id}`)}
          sx={{ borderRadius: 2.5, py: 1.2, fontWeight: 700 }}
        >
          Xem chi tiết
        </Button>
      </CardActions>
    </Card>
  );
};

export default CourseCard;
