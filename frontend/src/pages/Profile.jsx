import React, { useState } from 'react';
import { Container, Paper, Box, Typography, Avatar, TextField, Button, Alert, Grid, Chip, Divider } from '@mui/material';
import { Person as PersonIcon, Email as EmailIcon, Save as SaveIcon, Shield as ShieldIcon, Lock as LockIcon } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const Profile = () => {
  const { user, updateProfile, changePassword, loading } = useAuth();
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [avatar, setAvatar] = useState(user?.avatar || '');
  const [msg, setMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  // Change Password state
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [pwdMsg, setPwdMsg] = useState('');
  const [pwdErrorMsg, setPwdErrorMsg] = useState('');
  const [pwdLoading, setPwdLoading] = useState(false);

  const handleSave = async (e) => {
    e.preventDefault();
    setMsg('');
    setErrorMsg('');

    if (!fullName.trim()) {
      setErrorMsg('Họ và tên không được để trống!');
      return;
    }

    const res = await updateProfile(fullName, avatar);
    if (res.success) {
      setMsg('Cập nhật thông tin hồ sơ thành công!');
    } else {
      setErrorMsg(res.message);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setPwdMsg('');
    setPwdErrorMsg('');

    if (!currentPassword) {
      setPwdErrorMsg('Vui lòng nhập mật khẩu hiện tại!');
      return;
    }
    if (!newPassword || newPassword.length < 6) {
      setPwdErrorMsg('Mật khẩu mới phải có tối thiểu 6 ký tự!');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPwdErrorMsg('Mật khẩu mới và xác nhận mật khẩu không khớp!');
      return;
    }

    setPwdLoading(true);
    try {
      const res = await changePassword(currentPassword, newPassword, confirmPassword);
      if (res.success) {
        setPwdMsg('Đổi mật khẩu thành công!');
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');
      } else {
        setPwdErrorMsg(res.message);
      }
    } catch (err) {
      setPwdErrorMsg('Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại!');
    } finally {
      setPwdLoading(false);
    }
  };

  if (!user) return <Container sx={{ py: 6 }}><Typography>Bạn cần đăng nhập để xem hồ sơ.</Typography></Container>;

  return (
    <Container maxWidth="md" sx={{ py: 6 }} className="animate-fade-in">
      <Paper className="glass-card" sx={{ p: 4, borderRadius: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 3, mb: 4 }}>
          <Avatar alt={user.fullName} src={user.avatar || undefined} sx={{ width: 80, height: 80, bgcolor: 'primary.main', fontSize: '2rem', fontWeight: 700 }}>
            {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
          </Avatar>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>{user.fullName}</Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 1 }}>{user.email}</Typography>
            <Chip
              icon={<ShieldIcon fontSize="small" />}
              label={user.role === 'ADMIN' ? 'Vai trò: Quản trị viên (ADMIN)' : 'Vai trò: Học viên (STUDENT)'}
              color={user.role === 'ADMIN' ? 'error' : 'secondary'}
              sx={{ fontWeight: 700 }}
            />
          </Box>
        </Box>

        <Divider sx={{ mb: 4 }} />

        {msg && <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>{msg}</Alert>}
        {errorMsg && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{errorMsg}</Alert>}

        <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Chỉnh sửa thông tin cá nhân</Typography>

        <form onSubmit={handleSave}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Họ và tên"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                InputProps={{
                  startAdornment: <PersonIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                disabled
                label="Địa chỉ Email (Không thể thay đổi)"
                value={user.email}
                InputProps={{
                  startAdornment: <EmailIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Đường dẫn ảnh đại diện (Avatar URL)"
                placeholder="https://ui-avatars.com/api/?name=User"
                value={avatar}
                onChange={(e) => setAvatar(e.target.value)}
              />
            </Grid>

            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                disabled={loading}
                startIcon={<SaveIcon />}
                sx={{ py: 1.5, px: 4, fontWeight: 700 }}
              >
                {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
              </Button>
            </Grid>
          </Grid>
        </form>

        <Divider sx={{ my: 4 }} />

        {/* BẢO MẬT & ĐỔI MẬT KHẨU (FR-06) */}
        <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Đổi mật khẩu (Bảo mật tài khoản)</Typography>

        {pwdMsg && <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>{pwdMsg}</Alert>}
        {pwdErrorMsg && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{pwdErrorMsg}</Alert>}

        <form onSubmit={handlePasswordSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="password"
                label="Mật khẩu hiện tại"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                InputProps={{
                  startAdornment: <LockIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                type="password"
                label="Mật khẩu mới (Tối thiểu 6 ký tự)"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                InputProps={{
                  startAdornment: <LockIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                type="password"
                label="Xác nhận mật khẩu mới"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                InputProps={{
                  startAdornment: <LockIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Grid>

            <Grid item xs={12}>
              <Button
                type="submit"
                variant="outlined"
                color="primary"
                size="large"
                disabled={pwdLoading}
                startIcon={<LockIcon />}
                sx={{ py: 1.5, px: 4, fontWeight: 700 }}
              >
                {pwdLoading ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}
              </Button>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Container>
  );
};

export default Profile;
