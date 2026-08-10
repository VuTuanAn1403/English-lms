import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Alert,
  CircularProgress,
} from '@mui/material';
import { ArrowBack as ArrowBackIcon, Save as SaveIcon } from '@mui/icons-material';
import api from '../../services/api';

const AdminCourseForm = () => {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [level, setLevel] = useState('Cơ bản');
  const [imageUrl, setImageUrl] = useState('');

  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(isEdit);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    if (isEdit) {
      fetchCourseDetails();
    }
  }, [id]);

  const fetchCourseDetails = async () => {
    setFetching(true);
    try {
      const res = await api.get(`/api/v1/courses/${id}`);
      const course = res.data?.data;
      if (course) {
        setTitle(course.title || '');
        setDescription(course.description || '');
        setLevel(course.level || 'Cơ bản');
        setImageUrl(course.imageUrl || '');
      }
    } catch (err) {
      setErrorMsg('Không thể tải thông tin khóa học!');
    } finally {
      setFetching(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    if (!title.trim() || !description.trim()) {
      setErrorMsg('Vui lòng nhập đầy đủ Tiêu đề và Mô tả khóa học.');
      return;
    }

    setLoading(true);
    const payload = { title, description, level, imageUrl };

    try {
      if (isEdit) {
        await api.put(`/api/v1/courses/${id}`, payload);
      } else {
        await api.post('/api/v1/courses', payload);
      }
      navigate('/admin/courses');
    } catch (err) {
      const msg = err.response?.data?.message || 'Lưu khóa học thất bại. Vui lòng thử lại!';
      setErrorMsg(msg);
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/admin/courses')} sx={{ mb: 3 }}>
        Quay lại Danh sách Khóa học
      </Button>

      <Paper sx={{ p: 4, borderRadius: 4, maxWidth: 720, margin: '0 auto' }}>
        <Typography variant="h5" fontWeight="bold" gutterBottom>
          {isEdit ? 'Chỉnh Sửa Khóa Học' : 'Tạo Khóa Học Mới'}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Điền các thông tin thông số khóa học vào form dưới đây
        </Typography>

        {errorMsg && (
          <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
            {errorMsg}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Tiêu Đề Khóa Học"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Ví dụ: English Communication - Giao Tiếp Căn Bản"
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                multiline
                rows={4}
                label="Mô Tả Chi Tiết"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Nhập mô tả khóa học giúp học viên hiểu nội dung..."
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Trình Độ</InputLabel>
                <Select value={level} label="Trình Độ" onChange={(e) => setLevel(e.target.value)}>
                  <MenuItem value="Cơ bản">Cơ bản</MenuItem>
                  <MenuItem value="Trung cấp">Trung cấp</MenuItem>
                  <MenuItem value="Nâng cao">Nâng cao</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Đường Dẫn Ảnh Minh Họa (Image URL)"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                placeholder="https://images.unsplash.com/..."
              />
            </Grid>

            <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 2 }}>
              <Button variant="outlined" onClick={() => navigate('/admin/courses')}>
                Hủy Bỏ
              </Button>
              <Button
                type="submit"
                variant="contained"
                startIcon={<SaveIcon />}
                disabled={loading}
                sx={{ px: 4, fontWeight: 'bold' }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : isEdit ? 'LƯU CẬP NHẬT' : 'TẠO KHÓA HỌC'}
              </Button>
            </Grid>
          </Grid>
        </Box>
      </Paper>
    </Box>
  );
};

export default AdminCourseForm;
