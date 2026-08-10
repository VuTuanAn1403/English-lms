import React, { useState, useEffect } from 'react';
import { Container, Typography, Grid, Box, TextField, InputAdornment, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';
import CourseCard from '../components/CourseCard';
import Loading from '../components/Loading';
import api from '../services/api';

const sampleCourses = [
  {
    id: '550e8400-e29b-41d4-a716-446655440001',
    title: 'English Communication - Giao Tiếp Căn Bản',
    description: 'Khóa học giúp học viên tự tin giao tiếp tiếng Anh trong các tình huống hàng ngày với trợ lý AI hỗ trợ 24/7.',
    level: 'BEGINNER',
    effectivePrice: 299000,
    imageUrl: 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440002',
    title: 'IELTS Foundation 5.5+',
    description: 'Lộ trình luyện thi IELTS từ cơ bản đến 5.5+ đầy đủ 4 kỹ năng Nghe, Nói, Đọc, Viết kèm trắc nghiệm tự động.',
    level: 'INTERMEDIATE',
    effectivePrice: 499000,
    imageUrl: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440003',
    title: 'Business English - Tiếng Anh Thương Mại',
    description: 'Kỹ năng viết Email công việc, thuyết trình và đàm phán bằng tiếng Anh chuyên nghiệp cho người đi làm.',
    level: 'ADVANCED',
    effectivePrice: 599000,
    imageUrl: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440004',
    title: 'TOEIC 450+ Luyện Đề Tự Động',
    description: 'Khóa học tập trung vào 2 kỹ năng Listening & Reading với hệ thống bài tập trắc nghiệm sinh tự động.',
    level: 'INTERMEDIATE',
    effectivePrice: 399000,
    imageUrl: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=600&q=80'
  }
];

const Courses = () => {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('ALL');

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const res = await api.get('/api/v1/courses');
        setCourses(res.data?.data && res.data.data.length > 0 ? res.data.data : sampleCourses);
      } catch (err) {
        setCourses(sampleCourses);
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  const matchesLevel = (courseLevel, filterLevel) => {
    if (filterLevel === 'ALL') return true;
    const cLvl = (courseLevel || '').toUpperCase();
    const fLvl = filterLevel.toUpperCase();

    if (fLvl === 'BEGINNER') return cLvl === 'BEGINNER' || cLvl === 'CƠ BẢN';
    if (fLvl === 'INTERMEDIATE') return cLvl === 'INTERMEDIATE' || cLvl === 'TRUNG CẤP';
    if (fLvl === 'ADVANCED') return cLvl === 'ADVANCED' || cLvl === 'NÂNG CAO';
    return cLvl === fLvl;
  };

  const filteredCourses = courses.filter((c) => {
    const matchesSearch = c.title.toLowerCase().includes(searchTerm.toLowerCase()) || (c.description && c.description.toLowerCase().includes(searchTerm.toLowerCase()));
    return matchesSearch && matchesLevel(c.level, selectedLevel);
  });

  return (
    <Container maxWidth="lg" sx={{ py: 6 }} className="animate-fade-in">
      <Box sx={{ mb: 6, textAlign: 'center' }}>
        <Typography variant="h3" sx={{ fontWeight: 800, mb: 1, color: 'primary.main' }}>
          Danh sách Khóa học Tiếng Anh
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Nâng cao khả năng giao tiếp, ngữ pháp và luyện thi chứng chỉ cùng AI
        </Typography>
      </Box>

      {/* SEARCH AND FILTER BAR */}
      <Grid container spacing={2} sx={{ mb: 5 }}>
        <Grid item xs={12} sm={8}>
          <TextField
            fullWidth
            placeholder="Tìm kiếm khóa học theo tên hoặc nội dung..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }}
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <FormControl fullWidth>
            <InputLabel>Trình độ</InputLabel>
            <Select
              value={selectedLevel}
              label="Trình độ"
              onChange={(e) => setSelectedLevel(e.target.value)}
            >
              <MenuItem value="ALL">Tất cả trình độ</MenuItem>
              <MenuItem value="BEGINNER">Cơ bản (Beginner)</MenuItem>
              <MenuItem value="INTERMEDIATE">Trung cấp (Intermediate)</MenuItem>
              <MenuItem value="ADVANCED">Nâng cao (Advanced)</MenuItem>
            </Select>
          </FormControl>
        </Grid>
      </Grid>

      {loading ? (
        <Loading message="Đang tải danh sách khóa học..." />
      ) : filteredCourses.length > 0 ? (
        <Grid container spacing={3}>
          {filteredCourses.map((course) => (
            <Grid item key={course.id} xs={12} sm={6} md={4}>
              <CourseCard course={course} />
            </Grid>
          ))}
        </Grid>
      ) : (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            Không tìm thấy khóa học nào phù hợp với bộ lọc của bạn.
          </Typography>
        </Box>
      )}
    </Container>
  );
};

export default Courses;
