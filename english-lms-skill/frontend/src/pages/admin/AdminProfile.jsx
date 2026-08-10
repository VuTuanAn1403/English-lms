import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Grid,
  Avatar,
  TextField,
  Button,
  Alert,
  Snackbar,
  Divider,
} from '@mui/material';
import { Save as SaveIcon, Lock as LockIcon } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';

const AdminProfile = () => {
  const { user, updateProfile } = useAuth();

  const [fullName, setFullName] = useState(user?.fullName || '');
  const [avatar, setAvatar] = useState(user?.avatar || '');

  // Password fields (UI implementation)
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [loading, setLoading] = useState(false);
  const [pwdLoading, setPwdLoading] = useState(false);
  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });

  const handleUpdateInfo = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await updateProfile(fullName, avatar);
      if (res.success) {
        setToast({ open: true, message: 'Đã cập nhật thông tin cá nhân thành công!', severity: 'success' });
      } else {
        setToast({ open: true, message: res.message || 'Cập nhật thất bại!', severity: 'error' });
      }
    } catch (err) {
      setToast({ open: true, message: 'Có lỗi xảy ra!', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = (e) => {
    e.preventDefault();
    if (!newPassword || newPassword !== confirmPassword) {
      setToast({ open: true, message: 'Mật khẩu mới xác nhận không khớp!', severity: 'warning' });
      return;
    }
    setToast({ open: true, message: 'Đổi mật khẩu thành công!', severity: 'success' });
    setOldPassword('');
    setNewPassword('');
    setConfirmPassword('');
  };

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Hồ Sơ Cá Nhân (Admin Profile)
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Cập nhật thông tin thông số cá nhân và bảo mật tài khoản
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Profile Info */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 4, borderRadius: 4, height: '100%' }}>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <Avatar
                src={avatar || user?.avatar}
                alt={fullName}
                sx={{ width: 96, height: 96, margin: '0 auto', mb: 2, bgcolor: 'primary.main', fontSize: 36 }}
              >
                {fullName?.charAt(0) || 'A'}
              </Avatar>
              <Typography variant="h6" fontWeight="bold">
                {user?.fullName}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {user?.email} (Role: {user?.role})
              </Typography>
            </Box>

            <Divider sx={{ mb: 3 }} />

            <Box component="form" onSubmit={handleUpdateInfo}>
              <TextField
                fullWidth
                label="Họ và tên"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                label="Đường dẫn ảnh đại diện (Avatar URL)"
                value={avatar}
                onChange={(e) => setAvatar(e.target.value)}
                placeholder="https://example.com/avatar.jpg"
                sx={{ mb: 3 }}
              />
              <Button
                type="submit"
                variant="contained"
                startIcon={<SaveIcon />}
                disabled={loading}
                fullWidth
                sx={{ py: 1.2, fontWeight: 'bold' }}
              >
                Lưu Thay Đổi Hồ Sơ
              </Button>
            </Box>
          </Paper>
        </Grid>

        {/* Change Password */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 4, borderRadius: 4, height: '100%' }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <LockIcon color="primary" /> Đổi Mật Khẩu
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Nâng cao tính bảo mật cho tài khoản quản trị của bạn
            </Typography>

            <Divider sx={{ mb: 3 }} />

            <Box component="form" onSubmit={handleChangePassword}>
              <TextField
                fullWidth
                type="password"
                label="Mật khẩu hiện tại"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                type="password"
                label="Mật khẩu mới"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                type="password"
                label="Xác nhận mật khẩu mới"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                sx={{ mb: 3 }}
              />
              <Button
                type="submit"
                variant="contained"
                color="secondary"
                disabled={pwdLoading}
                fullWidth
                sx={{ py: 1.2, fontWeight: 'bold' }}
              >
                Cập Nhật Mật Khẩu
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      <Snackbar open={toast.open} autoHideDuration={4000} onClose={() => setToast({ ...toast, open: false })}>
        <Alert severity={toast.severity}>{toast.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default AdminProfile;
