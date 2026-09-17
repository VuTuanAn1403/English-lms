import React, { useState, useEffect } from 'react';
import { Container, Typography, Grid, Box, TextField, InputAdornment, MenuItem, Select, FormControl, InputLabel, Alert, Button } from '@mui/material';
import { Search as SearchIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import CourseCard from '../components/CourseCard';
import Loading from '../components/Loading';
import api from '../services/api';

const Courses = () => {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('ALL');

  const fetchCourses = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get('/api/v1/courses');
      setCourses(res.data?.data || []);
    } catch (err) {
      console.error('Error fetching courses:', err);
      setError('Không thể tải dữ liệu khóa học.');
      setCourses([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
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
    const matchesSearch = (c.title || '').toLowerCase().includes(searchTerm.toLowerCase()) || 
      (c.description && c.description.toLowerCase().includes(searchTerm.toLowerCase()));
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

      {error && (
        <Alert
          severity="error"
          sx={{ mb: 4, borderRadius: 2 }}
          action={
            <Button color="inherit" size="small" startIcon={<RefreshIcon />} onClick={fetchCourses}>
              Thử lại
            </Button>
          }
        >
          {error}
        </Alert>
      )}

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
      ) : !error ? (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            Không tìm thấy khóa học nào phù hợp với bộ lọc của bạn.
          </Typography>
        </Box>
      ) : null}
    </Container>
  );
};

export default Courses;
