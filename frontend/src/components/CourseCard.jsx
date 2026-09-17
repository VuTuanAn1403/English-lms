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
  Star as StarIcon,
  LocalOffer as OfferIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const CourseCard = ({ course }) => {
  const navigate = useNavigate();

  const getLevelColor = (level) => {
    switch (level?.toUpperCase()) {
      case 'BEGINNER':
      case 'CƠ BẢN':
        return 'success';
      case 'INTERMEDIATE':
      case 'TRUNG CẤP':
        return 'warning';
      case 'ADVANCED':
      case 'NÂNG CAO':
        return 'error';
      default:
        return 'primary';
    }
  };

  const formatLevelLabel = (level) => {
    switch (level?.toUpperCase()) {
      case 'BEGINNER':
      case 'CƠ BẢN':
        return 'Cơ bản';
      case 'INTERMEDIATE':
      case 'TRUNG CẤP':
        return 'Trung cấp';
      case 'ADVANCED':
      case 'NÂNG CAO':
        return 'Nâng cao';
      default:
        return level || 'Cơ bản';
    }
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null || amount === 0) return 'Miễn phí';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const defaultImage = 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80';
  const price = course.effectivePrice !== undefined ? course.effectivePrice : course.price;
  const isFree = course.isFree || price === 0;

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
          label={formatLevelLabel(course.level)}
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
        {isFree ? (
          <Chip
            label="Miễn Phí"
            color="success"
            size="small"
            sx={{
              position: 'absolute',
              top: 12,
              left: 12,
              fontWeight: 800,
              boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
            }}
          />
        ) : (
          <Chip
            label="Học Thử 5 Bài"
            color="primary"
            size="small"
            sx={{
              position: 'absolute',
              top: 12,
              left: 12,
              fontWeight: 800,
              boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
            }}
          />
        )}
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

        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mt: 'auto' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
            <BookIcon fontSize="small" color="primary" />
            <Typography variant="caption" sx={{ fontWeight: 600 }}>{course.totalLessons || 0} bài học</Typography>
          </Box>
          <Typography variant="subtitle1" sx={{ fontWeight: 900, color: isFree ? '#10B981' : 'primary.main' }}>
            {formatVND(price)}
          </Typography>
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
