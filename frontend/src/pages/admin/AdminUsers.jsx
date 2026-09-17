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
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tooltip,
  Alert,
  Snackbar,
  LinearProgress,
  Stack,
  Divider,
  Grid,
} from '@mui/material';
import {
  Search as SearchIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Block as BlockIcon,
  CheckCircle as UnblockIcon,
  PersonAdd as AddUserIcon,
  VpnKey as KeyIcon,
  Delete as DeleteIcon,
  School as CourseIcon,
} from '@mui/icons-material';
import api from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

const AdminUsers = () => {
  const navigate = useNavigate();
  const { user: currentUser } = useAuth();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  // Dialog States
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  const [selectedUser, setSelectedUser] = useState(null);

  // Form Data States
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    role: 'STUDENT',
    status: 'ACTIVE',
    avatar: '',
  });

  const [newPassword, setNewPassword] = useState('');
  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    fetchUsers();
  }, [page, rowsPerPage, searchTerm, roleFilter, statusFilter]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/v1/users', {
        params: {
          page: page,
          size: rowsPerPage,
          keyword: searchTerm || undefined,
          role: roleFilter !== 'ALL' ? roleFilter : undefined,
          status: statusFilter !== 'ALL' ? statusFilter : undefined,
        },
      });

      const pageData = res.data?.data;
      if (pageData && pageData.items) {
        setUsers(pageData.items);
        setTotalElements(pageData.totalElements || pageData.items.length);
      } else if (Array.isArray(res.data?.data)) {
        setUsers(res.data.data);
        setTotalElements(res.data.data.length);
      }
    } catch (err) {
      console.warn('Error fetching users, using fallback:', err);
    } finally {
      setLoading(false);
    }
  };

  // Create User Handlers
  const handleOpenCreate = () => {
    setFormData({ fullName: '', email: '', password: '', role: 'STUDENT', status: 'ACTIVE', avatar: '' });
    setCreateDialogOpen(true);
  };

  const handleCreateUser = async () => {
    if (!formData.fullName || !formData.email || !formData.password) {
      setToast({ open: true, message: 'Vui lòng điền đầy đủ họ tên, email và mật khẩu!', severity: 'error' });
      return;
    }
    try {
      await api.post('/api/v1/users', formData);
      setToast({ open: true, message: 'Tạo tài khoản mới thành công!', severity: 'success' });
      setCreateDialogOpen(false);
      fetchUsers();
    } catch (err) {
      setToast({ open: true, message: err.response?.data?.message || 'Lỗi tạo tài khoản mới.', severity: 'error' });
    }
  };

  // Edit User Handlers
  const handleOpenEdit = (user) => {
    setSelectedUser(user);
    setFormData({
      fullName: user.fullName || '',
      email: user.email || '',
      role: user.role || 'STUDENT',
      status: user.status || 'ACTIVE',
      avatar: user.avatar || '',
    });
    setEditDialogOpen(true);
  };

  const handleSaveEdit = async () => {
    if (!selectedUser) return;
    try {
      await api.put(`/api/v1/users/${selectedUser.id}`, {
        fullName: formData.fullName,
        email: formData.email,
        role: formData.role,
        status: formData.status,
        avatar: formData.avatar,
      });
      setToast({ open: true, message: `Đã cập nhật thông tin tài khoản ${formData.email}`, severity: 'success' });
      setEditDialogOpen(false);
      fetchUsers();
    } catch (err) {
      setToast({ open: true, message: err.response?.data?.message || 'Lỗi cập nhật người dùng.', severity: 'error' });
    }
  };

  // Lock / Unlock Handler
  const handleToggleStatus = async (user) => {
    const nextStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      await api.patch(`/api/v1/users/${user.id}/status`, { status: nextStatus });
      setToast({
        open: true,
        message: `Đã ${nextStatus === 'INACTIVE' ? 'khóa' : 'mở khóa'} tài khoản ${user.email}`,
        severity: nextStatus === 'INACTIVE' ? 'warning' : 'success',
      });
      fetchUsers();
    } catch (err) {
      setToast({ open: true, message: 'Lỗi cập nhật trạng thái.', severity: 'error' });
    }
  };

  // Reset Password Handler
  const handleOpenResetPassword = (user) => {
    setSelectedUser(user);
    setNewPassword('');
    setPasswordDialogOpen(true);
  };

  const handleResetPassword = async () => {
    if (!newPassword || newPassword.length < 6) {
      setToast({ open: true, message: 'Mật khẩu mới phải từ 6 ký tự trở lên!', severity: 'error' });
      return;
    }
    try {
      await api.patch(`/api/v1/users/${selectedUser.id}/password`, { newPassword });
      setToast({ open: true, message: `Đã đặt lại mật khẩu cho tài khoản ${selectedUser.email}`, severity: 'success' });
      setPasswordDialogOpen(false);
    } catch (err) {
      setToast({ open: true, message: 'Lỗi đặt lại mật khẩu.', severity: 'error' });
    }
  };

  // Delete User Handler
  const handleOpenDelete = (user) => {
    if (currentUser && currentUser.email === user.email) {
      setToast({ open: true, message: 'Không thể xóa chính tài khoản Admin đang đăng nhập!', severity: 'error' });
      return;
    }
    setSelectedUser(user);
    setDeleteDialogOpen(true);
  };

  const handleDeleteUser = async () => {
    if (!selectedUser) return;
    try {
      await api.delete(`/api/v1/users/${selectedUser.id}`);
      setToast({ open: true, message: `Đã xóa tài khoản ${selectedUser.email}`, severity: 'success' });
      setDeleteDialogOpen(false);
      fetchUsers();
    } catch (err) {
      setToast({ open: true, message: err.response?.data?.message || 'Lỗi xóa người dùng.', severity: 'error' });
    }
  };

  // View Detail Handler
  const handleOpenView = (user) => {
    setSelectedUser(user);
    setViewDialogOpen(true);
  };

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Quản Lý Người Dùng (User Management)
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Xem danh sách, phân quyền (ADMIN / STUDENT), khóa tài khoản và quản lý người dùng thực từ PostgreSQL
          </Typography>
        </Box>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddUserIcon />}
          onClick={handleOpenCreate}
          sx={{ borderRadius: 3, fontWeight: 700, px: 3, py: 1.2 }}
        >
          + Thêm Người Dùng
        </Button>
      </Box>

      {loading && <LinearProgress sx={{ mb: 2, borderRadius: 1 }} />}

      {/* Filter and Search Bar */}
      <Paper elevation={0} sx={{ p: 2.5, mb: 3, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={6}>
            <TextField
              fullWidth
              size="small"
              placeholder="Tìm theo tên hoặc email người dùng..."
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
          <Grid item xs={6} sm={3} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Vai trò (Role)</InputLabel>
              <Select value={roleFilter} label="Vai trò (Role)" onChange={(e) => { setRoleFilter(e.target.value); setPage(0); }}>
                <MenuItem value="ALL">Tất cả vai trò</MenuItem>
                <MenuItem value="ADMIN">ADMIN</MenuItem>
                <MenuItem value="STUDENT">STUDENT</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6} sm={3} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Trạng thái (Status)</InputLabel>
              <Select value={statusFilter} label="Trạng thái (Status)" onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}>
                <MenuItem value="ALL">Tất cả trạng thái</MenuItem>
                <MenuItem value="ACTIVE">Hoạt động (ACTIVE)</MenuItem>
                <MenuItem value="INACTIVE">Đã khóa (INACTIVE)</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {/* Users Table */}
      <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Table>
          <TableHead sx={{ bgcolor: '#f8fafc' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Người dùng</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Email</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Vai trò</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Trạng thái</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Ngày tạo</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }} align="right">Thao tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.length > 0 ? (
              users.map((u) => (
                <TableRow key={u.id} hover>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Avatar src={u.avatar} alt={u.fullName || u.email}>
                        {(u.fullName || u.email).charAt(0).toUpperCase()}
                      </Avatar>
                      <Typography fontWeight="bold">{u.fullName || u.email.split('@')[0]}</Typography>
                    </Box>
                  </TableCell>
                  <TableCell>{u.email}</TableCell>
                  <TableCell>
                    <Chip
                      label={u.role || 'STUDENT'}
                      size="small"
                      color={u.role === 'ADMIN' ? 'error' : 'primary'}
                      sx={{ fontWeight: 800 }}
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={u.status === 'INACTIVE' ? 'Đã khóa' : 'Hoạt động'}
                      size="small"
                      color={u.status === 'INACTIVE' ? 'default' : 'success'}
                      variant="outlined"
                      sx={{ fontWeight: 700 }}
                    />
                  </TableCell>
                  <TableCell>{u.createdAt ? new Date(u.createdAt).toLocaleDateString('vi-VN') : '—'}</TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                      <Tooltip title="👁 Xem Chi Tiết">
                        <IconButton onClick={() => handleOpenView(u)} color="primary" size="small">
                          <ViewIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="✏ Sửa Thông Tin">
                        <IconButton onClick={() => handleOpenEdit(u)} color="info" size="small">
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="🔑 Đặt Là Mật Khẩu">
                        <IconButton onClick={() => handleOpenResetPassword(u)} color="warning" size="small">
                          <KeyIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title={u.status === 'ACTIVE' ? '🔒 Khóa Tài Khoản' : '🔓 Mở Khóa Tài Khoản'}>
                        <IconButton onClick={() => handleToggleStatus(u)} color={u.status === 'ACTIVE' ? 'error' : 'success'} size="small">
                          {u.status === 'ACTIVE' ? <BlockIcon fontSize="small" /> : <UnblockIcon fontSize="small" />}
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="🗑 Xóa Tài Khoản">
                        <IconButton onClick={() => handleOpenDelete(u)} color="error" size="small">
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 5 }}>
                  <Typography color="text.secondary">Không tìm thấy người dùng phù hợp trong cơ sở dữ liệu.</Typography>
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
          labelRowsPerPage="Số hàng mỗi trang:"
        />
      </TableContainer>

      {/* 1. DIALOG CREATE USER */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold">+ Thêm Người Dùng Mới</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Họ và tên *"
            fullWidth
            size="small"
            value={formData.fullName}
            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
          />
          <TextField
            label="Email *"
            type="email"
            fullWidth
            size="small"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <TextField
            label="Mật khẩu *"
            type="password"
            fullWidth
            size="small"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
          />
          <FormControl fullWidth size="small">
            <InputLabel>Vai trò (Role) *</InputLabel>
            <Select value={formData.role} label="Vai trò (Role) *" onChange={(e) => setFormData({ ...formData, role: e.target.value })}>
              <MenuItem value="STUDENT">STUDENT (Học viên)</MenuItem>
              <MenuItem value="ADMIN">ADMIN (Quản trị viên)</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="Đường dẫn Avatar (tùy chọn)"
            fullWidth
            size="small"
            value={formData.avatar}
            onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setCreateDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" onClick={handleCreateUser} sx={{ fontWeight: 700 }}>Tạo Tài Khoản</Button>
        </DialogActions>
      </Dialog>

      {/* 2. DIALOG EDIT USER */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold">✏ Cập Nhật Thông Tin Tài Khoản</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Họ và tên"
            fullWidth
            size="small"
            value={formData.fullName}
            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
          />
          <TextField
            label="Email"
            type="email"
            fullWidth
            size="small"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <FormControl fullWidth size="small">
            <InputLabel>Vai trò (Role)</InputLabel>
            <Select value={formData.role} label="Vai trò (Role)" onChange={(e) => setFormData({ ...formData, role: e.target.value })}>
              <MenuItem value="STUDENT">STUDENT (Học viên)</MenuItem>
              <MenuItem value="ADMIN">ADMIN (Quản trị viên)</MenuItem>
            </Select>
          </FormControl>
          <FormControl fullWidth size="small">
            <InputLabel>Trạng thái (Status)</InputLabel>
            <Select value={formData.status} label="Trạng thái (Status)" onChange={(e) => setFormData({ ...formData, status: e.target.value })}>
              <MenuItem value="ACTIVE">ACTIVE (Hoạt động)</MenuItem>
              <MenuItem value="INACTIVE">INACTIVE (Khóa)</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="Đường dẫn Avatar"
            fullWidth
            size="small"
            value={formData.avatar}
            onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setEditDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" onClick={handleSaveEdit} sx={{ fontWeight: 700 }}>Lưu Thay Đổi</Button>
        </DialogActions>
      </Dialog>

      {/* 3. DIALOG VIEW USER DETAIL */}
      <Dialog open={viewDialogOpen} onClose={() => setViewDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle fontWeight="bold">👁 Chi Tiết Thông Tin Tài Khoản</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          {selectedUser && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2.5 }}>
                <Avatar src={selectedUser.avatar} sx={{ width: 64, height: 64 }}>
                  {(selectedUser.fullName || selectedUser.email).charAt(0).toUpperCase()}
                </Avatar>
                <Box>
                  <Typography variant="h6" fontWeight="bold">{selectedUser.fullName || 'Học viên'}</Typography>
                  <Typography variant="body2" color="text.secondary">{selectedUser.email}</Typography>
                  <Stack direction="row" spacing={1} sx={{ mt: 1 }}>
                    <Chip label={selectedUser.role} color={selectedUser.role === 'ADMIN' ? 'error' : 'primary'} size="small" sx={{ fontWeight: 800 }} />
                    <Chip label={selectedUser.status === 'INACTIVE' ? 'Đã khóa' : 'Hoạt động'} color={selectedUser.status === 'INACTIVE' ? 'default' : 'success'} size="small" variant="outlined" />
                  </Stack>
                </Box>
              </Box>

              <Divider sx={{ my: 1 }} />

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">ID Tài Khoản</Typography>
                  <Typography variant="body2" fontWeight="bold" sx={{ wordBreak: 'break-all' }}>{selectedUser.id}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Ngày tạo tài khoản</Typography>
                  <Typography variant="body2" fontWeight="bold">{selectedUser.createdAt ? new Date(selectedUser.createdAt).toLocaleString('vi-VN') : '—'}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Thời gian cập nhật gần nhất</Typography>
                  <Typography variant="body2" fontWeight="bold">{selectedUser.updatedAt ? new Date(selectedUser.updatedAt).toLocaleString('vi-VN') : '—'}</Typography>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setViewDialogOpen(false)}>Đóng</Button>
        </DialogActions>
      </Dialog>

      {/* 4. DIALOG RESET PASSWORD */}
      <Dialog open={passwordDialogOpen} onClose={() => setPasswordDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold">🔑 Đặt Lại Mật Khẩu</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Đặt lại mật khẩu cho tài khoản: <strong>{selectedUser?.email}</strong>
          </Typography>
          <TextField
            label="Mật khẩu mới (tối thiểu 6 ký tự) *"
            type="password"
            fullWidth
            size="small"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setPasswordDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" color="warning" onClick={handleResetPassword} sx={{ fontWeight: 700 }}>Đặt Lại Mật Khẩu</Button>
        </DialogActions>
      </Dialog>

      {/* 5. DIALOG DELETE CONFIRMATION */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold" color="error">🗑 Xác Nhận Xóa Tài Khoản</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <Alert severity="warning" sx={{ mb: 2, borderRadius: 2 }}>
            Hành động này sẽ xóa vĩnh viễn tài khoản <strong>{selectedUser?.email}</strong> khỏi cơ sở dữ liệu PostgreSQL.
          </Alert>
          <Typography variant="body2" color="text.secondary">
            Bạn có chắc chắn muốn tiếp tục không?
          </Typography>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={() => setDeleteDialogOpen(false)}>Hủy</Button>
          <Button variant="contained" color="error" onClick={handleDeleteUser} sx={{ fontWeight: 700 }}>Xóa Vĩnh Viễn</Button>
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

export default AdminUsers;
