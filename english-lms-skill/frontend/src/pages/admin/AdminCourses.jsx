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
  TextField,
  InputAdornment,
  Chip,
  IconButton,
  Button,
  Avatar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tooltip,
  Alert,
  Snackbar,
  CircularProgress,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  MenuBook as CourseIcon,
} from '@mui/icons-material';
import api from '../../services/api';

const AdminCourses = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [levelFilter, setLevelFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState(null);
  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    fetchCourses();
  }, []);

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/courses');
      setCourses(response.data?.data || []);
    } catch (err) {
      setToast({ open: true, message: 'Lỗi tải danh sách khóa học!', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDeleteDialog = (course) => {
    setSelectedCourse(course);
    setDeleteDialogOpen(true);
  };

  const handleDeleteCourse = async () => {
    if (!selectedCourse) return;
    try {
      await api.delete(`/api/v1/courses/${selectedCourse.id}`);
      setToast({ open: true, message: `Đã xóa khóa học "${selectedCourse.title}" thành công`, severity: 'success' });
      fetchCourses();
    } catch (err) {
      setToast({ open: true, message: 'Xóa khóa học thất bại!', severity: 'error' });
    } finally {
      setDeleteDialogOpen(false);
    }
  };

  const filteredCourses = courses.filter((c) => {
    const matchesSearch = c.title.toLowerCase().includes(searchTerm.toLowerCase()) || c.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesLevel = levelFilter === 'ALL' || c.level === levelFilter;
    return matchesSearch && matchesLevel;
  });

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Quản Lý Khóa Học
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Tạo mới, chỉnh sửa, xóa và quản lý danh sách khóa học
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/admin/courses/new')}
          sx={{ borderRadius: 3 }}
        >
          Thêm Khóa Học Mới
        </Button>
      </Box>

      {/* Filter and Search */}
      <Paper sx={{ p: 2, mb: 3, borderRadius: 3 }}>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <TextField
            size="small"
            placeholder="Tìm theo tiêu đề hoặc mô tả..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            sx={{ flexGrow: 1, minWidth: 240 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
          <FormControl size="small" sx={{ minWidth: 160 }}>
            <InputLabel>Trình độ (Level)</InputLabel>
            <Select value={levelFilter} label="Trình độ (Level)" onChange={(e) => setLevelFilter(e.target.value)}>
              <MenuItem value="ALL">Tất cả trình độ</MenuItem>
              <MenuItem value="Cơ bản">Cơ bản</MenuItem>
              <MenuItem value="Trung cấp">Trung cấp</MenuItem>
              <MenuItem value="Nâng cao">Nâng cao</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </Paper>

      {/* Table */}
      <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Table>
            <TableHead sx={{ bgcolor: 'action.hover' }}>
              <TableRow>
                <TableCell fontWeight="bold">Khóa Học</TableCell>
                <TableCell fontWeight="bold">Trình Độ</TableCell>
                <TableCell fontWeight="bold">Mô Tả</TableCell>
                <TableCell fontWeight="bold" align="right">Thao Tác</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredCourses.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} align="center" sx={{ py: 3 }}>
                    Chưa có khóa học nào.
                  </TableCell>
                </TableRow>
              ) : (
                filteredCourses
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((course) => (
                    <TableRow key={course.id} hover>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                          <Avatar
                            src={course.imageUrl}
                            variant="rounded"
                            sx={{ width: 48, height: 48, bgcolor: 'primary.light' }}
                          >
                            <CourseIcon />
                          </Avatar>
                          <Typography fontWeight="bold">{course.title}</Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={course.level || 'Cơ bản'}
                          size="small"
                          color="primary"
                          variant="soft"
                        />
                      </TableCell>
                      <TableCell sx={{ maxWidth: 300 }}>
                        <Typography variant="body2" noWrap>
                          {course.description}
                        </Typography>
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Chỉnh Sửa">
                          <IconButton onClick={() => navigate(`/admin/courses/edit/${course.id}`)} color="primary">
                            <EditIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Xóa Khóa Học">
                          <IconButton onClick={() => handleOpenDeleteDialog(course)} color="error">
                            <DeleteIcon />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))
              )}
            </TableBody>
          </Table>
        )}
        <TablePagination
          component="div"
          count={filteredCourses.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
        />
      </TableContainer>

      {/* Confirm Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle fontWeight="bold">Xác Nhận Xóa Khóa Học</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bạn có chắc chắn muốn xóa khóa học <strong>"{selectedCourse?.title}"</strong>? Thao tác này sẽ không thể hoàn tác!
          </DialogContentText>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setDeleteDialogOpen(false)}>Hủy Bỏ</Button>
          <Button variant="contained" color="error" onClick={handleDeleteCourse}>
            Xóa Khóa Học
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={toast.open}
        autoHideDuration={4000}
        onClose={() => setToast({ ...toast, open: false })}
      >
        <Alert severity={toast.severity}>{toast.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default AdminCourses;
