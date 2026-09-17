import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Tooltip,
  Alert,
  Snackbar,
  LinearProgress,
  Stack,
  Grid,
  Divider,
  InputAdornment,
} from '@mui/material';
import {
  Add as AddIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  ArrowUpward as ArrowUpIcon,
  ArrowDownward as ArrowDownIcon,
  OndemandVideo as VideoIcon,
  Description as DocumentIcon,
  Search as SearchIcon,
  School as CourseIcon,
  AccessTime as TimeIcon,
} from '@mui/icons-material';
import api from '../../services/api';

const AdminLessons = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState('ALL');
  const [publishFilter, setPublishFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const [lessons, setLessons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  // Dialog States
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  const [selectedLesson, setSelectedLesson] = useState(null);

  // Form State
  const [formData, setFormData] = useState({
    courseId: '',
    title: '',
    description: '',
    content: '',
    videoUrl: '',
    documentUrl: '',
    duration: 15,
    orderIndex: 1,
    isPublished: true,
  });

  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    fetchLessons();
  }, [page, rowsPerPage, selectedCourseId, publishFilter, searchTerm]);

  const fetchCourses = async () => {
    try {
      const res = await api.get('/api/v1/courses');
      setCourses(res.data?.data || []);
    } catch (err) {
      console.warn('Lỗi nạp danh sách khóa học:', err);
    }
  };

  const fetchLessons = async () => {
    setLoading(true);
    try {
      const isPubParam = publishFilter === 'ALL' ? undefined : publishFilter === 'PUBLISHED';
      const courseIdParam = selectedCourseId !== 'ALL' ? selectedCourseId : undefined;

      const res = await api.get('/api/v1/lessons', {
        params: {
          page: page,
          size: rowsPerPage,
          keyword: searchTerm || undefined,
          courseId: courseIdParam,
          isPublished: isPubParam,
        },
      });

      const pageData = res.data?.data;
      if (pageData && pageData.items) {
        setLessons(pageData.items);
        setTotalElements(pageData.totalElements || pageData.items.length);
      } else if (Array.isArray(res.data?.data)) {
        setLessons(res.data.data);
        setTotalElements(res.data.data.length);
      }
    } catch (err) {
      console.warn('Lỗi nạp danh sách bài học:', err);
    } finally {
      setLoading(false);
    }
  };

  // Create Handlers
  const handleOpenCreate = () => {
    const defaultCourseId = courses.length > 0 ? courses[0].id : '';
    setFormData({
      courseId: selectedCourseId !== 'ALL' ? selectedCourseId : defaultCourseId,
      title: '',
      description: '',
      content: '',
      videoUrl: '',
      documentUrl: '',
      duration: 15,
      orderIndex: lessons.length + 1,
      isPublished: true,
    });
    setCreateDialogOpen(true);
  };

  const handleCreateLesson = async () => {
    if (!formData.title || !formData.courseId) {
      setToast({ open: true, message: 'Vui lòng chọn khóa học và nhập tiêu đề bài học!', severity: 'error' });
      return;
    }
    try {
      await api.post('/api/v1/lessons', {
        ...formData,
        duration: Number(formData.duration),
        orderIndex: Number(formData.orderIndex),
      });
      setToast({ open: true, message: 'Tạo bài học mới thành công!', severity: 'success' });
      setCreateDialogOpen(false);
      fetchLessons();
    } catch (err) {
      setToast({ open: true, message: err.response?.data?.message || 'Lỗi tạo bài học mới.', severity: 'error' });
    }
  };

  // Edit Handlers
  const handleOpenEdit = (lesson) => {
    setSelectedLesson(lesson);
    setFormData({
      courseId: lesson.courseId || '',
      title: lesson.title || '',
      description: lesson.description || '',
      content: lesson.content || '',
      videoUrl: lesson.videoUrl || '',
      documentUrl: lesson.documentUrl || lesson.pdfUrl || '',
      duration: lesson.duration || 15,
      orderIndex: lesson.orderIndex || lesson.lessonOrder || 1,
      isPublished: lesson.isPublished !== false,
    });
    setEditDialogOpen(true);
  };

  const handleSaveEdit = async () => {
    if (!selectedLesson) return;
    try {
      await api.put(`/api/v1/lessons/${selectedLesson.id}`, {
        ...formData,
        duration: Number(formData.duration),
        orderIndex: Number(formData.orderIndex),
      });
      setToast({ open: true, message: `Đã cập nhật bài học "${formData.title}"`, severity: 'success' });
      setEditDialogOpen(false);
      fetchLessons();
    } catch (err) {
      setToast({ open: true, message: err.response?.data?.message || 'Lỗi cập nhật bài học.', severity: 'error' });
    }
  };

  // Reorder Move Up / Move Down
  const handleMoveUp = async (lesson) => {
    try {
      await api.patch(`/api/v1/lessons/${lesson.id}/move-up`);
      setToast({ open: true, message: `Đã đẩy bài học "${lesson.title}" lên trước`, severity: 'info' });
      fetchLessons();
    } catch (err) {
      setToast({ open: true, message: 'Không thể thay đổi thứ tự.', severity: 'error' });
    }
  };

  const handleMoveDown = async (lesson) => {
    try {
      await api.patch(`/api/v1/lessons/${lesson.id}/move-down`);
      setToast({ open: true, message: `Đã đẩy bài học "${lesson.title}" xuống sau`, severity: 'info' });
      fetchLessons();
    } catch (err) {
      setToast({ open: true, message: 'Không thể thay đổi thứ tự.', severity: 'error' });
    }
  };

  // Delete Handler
  const handleOpenDelete = (lesson) => {
    setSelectedLesson(lesson);
    setDeleteDialogOpen(true);
  };

  const handleDeleteLesson = async () => {
    if (!selectedLesson) return;
    try {
      await api.delete(`/api/v1/lessons/${selectedLesson.id}`);
      setToast({ open: true, message: `Đã xóa bài học "${selectedLesson.title}" thành công`, severity: 'success' });
      setDeleteDialogOpen(false);
      fetchLessons();
    } catch (err) {
      setToast({ open: true, message: 'Lỗi xóa bài học.', severity: 'error' });
    }
  };

  // View Detail Handler
  const handleOpenView = (lesson) => {
    setSelectedLesson(lesson);
    setViewDialogOpen(true);
  };

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Quản Lý Bài Học (Lesson Management)
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Xem danh sách bài học, lọc theo khóa học, sắp xếp thứ tự và quản lý học liệu video/PDF
          </Typography>
        </Box>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleOpenCreate}
          sx={{ borderRadius: 3, fontWeight: 700, px: 3, py: 1.2 }}
        >
          + Thêm Bài Học Mới
        </Button>
      </Box>

      {loading && <LinearProgress sx={{ mb: 2, borderRadius: 1 }} />}

      {/* Filter and Search Bar */}
      <Paper elevation={0} sx={{ p: 2.5, mb: 3, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={5} md={5}>
            <TextField
              fullWidth
              size="small"
              placeholder="Tìm theo tiêu đề hoặc nội dung bài học..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(0); }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={6} sm={4} md={4}>
            <FormControl fullWidth size="small">
              <InputLabel>Khóa Học (Course)</InputLabel>
              <Select value={selectedCourseId} label="Khóa Học (Course)" onChange={(e) => { setSelectedCourseId(e.target.value); setPage(0); }}>
                <MenuItem value="ALL">Tất cả khóa học ({courses.length})</MenuItem>
                {courses.map((c) => (
                  <MenuItem key={c.id} value={c.id}>
                    {c.title}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6} sm={3} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Trạng thái (Publish)</InputLabel>
              <Select value={publishFilter} label="Trạng thái (Publish)" onChange={(e) => { setPublishFilter(e.target.value); setPage(0); }}>
                <MenuItem value="ALL">Tất cả trạng thái</MenuItem>
                <MenuItem value="PUBLISHED">Đã xuất bản (Published)</MenuItem>
                <MenuItem value="UNPUBLISHED">Bản nháp (Draft)</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {/* Lessons Table */}
      <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Table>
          <TableHead sx={{ bgcolor: '#f8fafc' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>STT / Order</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Tiêu Đề Bài Học</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Khóa Học</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Thời Lượng</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Thứ Tự (Sort)</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Trạng Thái</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Ngày Tạo</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }} align="right">Thao Tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {lessons.length > 0 ? (
              lessons.map((l, index) => (
                <TableRow key={l.id} hover>
                  <TableCell fontWeight="bold">#{l.orderIndex || l.lessonOrder || index + 1}</TableCell>
                  <TableCell>
                    <Typography fontWeight="bold">{l.title}</Typography>
                    <Typography variant="caption" color="text.secondary" noWrap sx={{ maxWidth: 260, display: 'block' }}>
                      {l.description || l.content || 'Chưa có mô tả'}
                    </Typography>
                    <Stack direction="row" spacing={0.5} sx={{ mt: 0.5 }}>
                      {l.videoUrl && <Chip icon={<VideoIcon />} label="Video" size="small" color="primary" variant="outlined" sx={{ height: 20, fontSize: 10 }} />}
                      {(l.documentUrl || l.pdfUrl) && <Chip icon={<DocumentIcon />} label="Doc" size="small" color="error" variant="outlined" sx={{ height: 20, fontSize: 10 }} />}
                    </Stack>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" fontWeight="bold">{l.courseTitle || 'Khóa học tiếng Anh'}</Typography>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                      <TimeIcon fontSize="small" color="action" />
                      <Typography variant="body2">{l.duration || 15} phút</Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={0.5}>
                      <Tooltip title="Đẩy Bài Học Lên Trên">
                        <IconButton size="small" onClick={() => handleMoveUp(l)} color="primary">
                          <ArrowUpIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Đẩy Bài Học Xuống Dưới">
                        <IconButton size="small" onClick={() => handleMoveDown(l)} color="primary">
                          <ArrowDownIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Stack>
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={l.isPublished !== false ? 'Xuất bản' : 'Nháp'}
                      size="small"
                      color={l.isPublished !== false ? 'success' : 'default'}
                      variant={l.isPublished !== false ? 'filled' : 'outlined'}
                      sx={{ fontWeight: 700 }}
                    />
                  </TableCell>
                  <TableCell>{l.createdAt ? new Date(l.createdAt).toLocaleDateString('vi-VN') : '—'}</TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                      <Tooltip title="👁 Xem Chi Tiết">
                        <IconButton onClick={() => handleOpenView(l)} color="primary" size="small">
                          <ViewIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="✏ Chỉnh Sửa">
                        <IconButton onClick={() => handleOpenEdit(l)} color="info" size="small">
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="🗑 Xóa Bài Học">
                        <IconButton onClick={() => handleOpenDelete(l)} color="error" size="small">
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={8} align="center" sx={{ py: 5 }}>
                  <Typography color="text.secondary">Không tìm thấy bài học nào phù hợp trong cơ sở dữ liệu.</Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
          labelRowsPerPage="Số bài học mỗi trang:"
        />
      </TableContainer>

      {/* 1. DIALOG CREATE LESSON */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle fontWeight="bold">+ Thêm Bài Học Mới</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <FormControl fullWidth size="small">
            <InputLabel>Khóa học (Course) *</InputLabel>
            <Select value={formData.courseId} label="Khóa học (Course) *" onChange={(e) => setFormData({ ...formData, courseId: e.target.value })}>
              {courses.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.title}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Tiêu đề bài học *"
            fullWidth
            size="small"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="Ví dụ: Lesson 1: Basic Greetings"
          />
          <TextField
            label="Mô tả tóm tắt"
            fullWidth
            multiline
            rows={2}
            size="small"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
          <TextField
            label="Nội dung bài giảng chi tiết"
            fullWidth
            multiline
            rows={4}
            size="small"
            value={formData.content}
            onChange={(e) => setFormData({ ...formData, content: e.target.value })}
          />
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                label="Đường dẫn Video URL"
                fullWidth
                size="small"
                value={formData.videoUrl}
                onChange={(e) => setFormData({ ...formData, videoUrl: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Đường dẫn Tài liệu URL"
                fullWidth
                size="small"
                value={formData.documentUrl}
                onChange={(e) => setFormData({ ...formData, documentUrl: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Thời lượng (phút)"
                type="number"
                fullWidth
                size="small"
                value={formData.duration}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Thứ tự bài học (Order Index)"
                type="number"
                fullWidth
                size="small"
                value={formData.orderIndex}
                onChange={(e) => setFormData({ ...formData, orderIndex: e.target.value })}
              />
            </Grid>
          </Grid>
          <FormControl fullWidth size="small">
            <InputLabel>Trạng thái Xuất bản</InputLabel>
            <Select value={formData.isPublished} label="Trạng thái Xuất bản" onChange={(e) => setFormData({ ...formData, isPublished: e.target.value === true || e.target.value === 'true' })}>
              <MenuItem value={true}>Xuất bản ngay (Published)</MenuItem>
              <MenuItem value={false}>Lưu nháp (Draft)</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setCreateDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" onClick={handleCreateLesson} sx={{ fontWeight: 700 }}>Tạo Bài Học</Button>
        </DialogActions>
      </Dialog>

      {/* 2. DIALOG EDIT LESSON */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle fontWeight="bold">✏ Chỉnh Sửa Bài Học</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <FormControl fullWidth size="small">
            <InputLabel>Khóa học (Course)</InputLabel>
            <Select value={formData.courseId} label="Khóa học (Course)" onChange={(e) => setFormData({ ...formData, courseId: e.target.value })}>
              {courses.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.title}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Tiêu đề bài học"
            fullWidth
            size="small"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          />
          <TextField
            label="Mô tả tóm tắt"
            fullWidth
            multiline
            rows={2}
            size="small"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
          <TextField
            label="Nội dung bài giảng chi tiết"
            fullWidth
            multiline
            rows={4}
            size="small"
            value={formData.content}
            onChange={(e) => setFormData({ ...formData, content: e.target.value })}
          />
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                label="Đường dẫn Video URL"
                fullWidth
                size="small"
                value={formData.videoUrl}
                onChange={(e) => setFormData({ ...formData, videoUrl: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Đường dẫn Tài liệu URL"
                fullWidth
                size="small"
                value={formData.documentUrl}
                onChange={(e) => setFormData({ ...formData, documentUrl: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Thời lượng (phút)"
                type="number"
                fullWidth
                size="small"
                value={formData.duration}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Thứ tự bài học (Order Index)"
                type="number"
                fullWidth
                size="small"
                value={formData.orderIndex}
                onChange={(e) => setFormData({ ...formData, orderIndex: e.target.value })}
              />
            </Grid>
          </Grid>
          <FormControl fullWidth size="small">
            <InputLabel>Trạng thái Xuất bản</InputLabel>
            <Select value={formData.isPublished} label="Trạng thái Xuất bản" onChange={(e) => setFormData({ ...formData, isPublished: e.target.value === true || e.target.value === 'true' })}>
              <MenuItem value={true}>Xuất bản (Published)</MenuItem>
              <MenuItem value={false}>Lưu nháp (Draft)</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setEditDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" onClick={handleSaveEdit} sx={{ fontWeight: 700 }}>Lưu Thay Đổi</Button>
        </DialogActions>
      </Dialog>

      {/* 3. DIALOG VIEW LESSON DETAIL */}
      <Dialog open={viewDialogOpen} onClose={() => setViewDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle fontWeight="bold">👁 Chi Tiết Thông Tin Bài Học</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          {selectedLesson && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="h6" fontWeight="bold">{selectedLesson.title}</Typography>
                  <Typography variant="body2" color="text.secondary">Khóa học: {selectedLesson.courseTitle || 'Chưa xác định'}</Typography>
                </Box>
                <Chip
                  label={selectedLesson.isPublished !== false ? 'Đã Xuất Bản' : 'Bản Nháp'}
                  color={selectedLesson.isPublished !== false ? 'success' : 'default'}
                  sx={{ fontWeight: 800 }}
                />
              </Box>

              <Divider />

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Mã ID Bài Học</Typography>
                  <Typography variant="body2" fontWeight="bold" sx={{ wordBreak: 'break-all' }}>{selectedLesson.id}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Thứ Tự / Thời Lượng</Typography>
                  <Typography variant="body2" fontWeight="bold">Thứ tự: #{selectedLesson.orderIndex || selectedLesson.lessonOrder} | {selectedLesson.duration || 15} phút</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="caption" color="text.secondary">Mô Tả Tóm Tắt</Typography>
                  <Typography variant="body2">{selectedLesson.description || 'Chưa có mô tả'}</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="caption" color="text.secondary">Nội Dung Bài Học</Typography>
                  <Paper variant="outlined" sx={{ p: 2, bgcolor: '#f8fafc', maxHeight: 200, overflowY: 'auto' }}>
                    <Typography variant="body2" sx={{ whiteSpace: 'pre-line' }}>{selectedLesson.content || 'Chưa có nội dung'}</Typography>
                  </Paper>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Video URL</Typography>
                  <Typography variant="body2" color="primary" sx={{ wordBreak: 'break-all' }}>{selectedLesson.videoUrl || 'Không có'}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Document PDF URL</Typography>
                  <Typography variant="body2" color="error" sx={{ wordBreak: 'break-all' }}>{selectedLesson.documentUrl || selectedLesson.pdfUrl || 'Không có'}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Ngày Tạo</Typography>
                  <Typography variant="body2">{selectedLesson.createdAt ? new Date(selectedLesson.createdAt).toLocaleString('vi-VN') : '—'}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Ngày Cập Nhật</Typography>
                  <Typography variant="body2">{selectedLesson.updatedAt ? new Date(selectedLesson.updatedAt).toLocaleString('vi-VN') : '—'}</Typography>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setViewDialogOpen(false)}>Đóng</Button>
        </DialogActions>
      </Dialog>

      {/* 4. DIALOG DELETE CONFIRMATION */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold" color="error">🗑 Xác Nhận Xóa Bài Học</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <Alert severity="warning" sx={{ mb: 2, borderRadius: 2 }}>
            Hành động này sẽ xóa vĩnh viễn bài học <strong>"{selectedLesson?.title}"</strong> khỏi cơ sở dữ liệu PostgreSQL.
          </Alert>
          <Typography variant="body2" color="text.secondary">
            Nếu học viên đã thực hiện bài học này, hệ thống sẽ tự động dọn dẹp các bản ghi tiến độ liên quan để đảm bảo không bị lỗi Foreign Key.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setDeleteDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" color="error" onClick={handleDeleteLesson} sx={{ fontWeight: 700 }}>Xóa Vĩnh Viễn</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={toast.open} autoHideDuration={4000} onClose={() => setToast({ ...toast, open: false })}>
        <Alert severity={toast.severity}>{toast.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default AdminLessons;
