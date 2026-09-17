import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Switch,
  FormControlLabel,
  Divider,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Snackbar,
  Alert,
  Grid,
} from '@mui/material';
import {
  Settings as SettingsIcon,
  Palette as ThemeIcon,
  Language as LanguageIcon,
  Notifications as NotificationIcon,
  Security as SecurityIcon,
} from '@mui/icons-material';
import { useColorMode } from '../../contexts/ThemeContext';

const AdminSettings = () => {
  const { mode, toggleColorMode } = useColorMode();
  const [language, setLanguage] = useState('vi');
  const [emailNotify, setEmailNotify] = useState(true);
  const [systemAlert, setSystemAlert] = useState(true);

  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });

  const handleSaveSettings = () => {
    setToast({ open: true, message: 'Đã lưu cấu hình hệ thống thành công!', severity: 'success' });
  };

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Cài Đặt Hệ Thống (System Settings)
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Quản lý giao diện, ngôn ngữ, thông báo và cấu hình hệ thống
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 4, borderRadius: 4 }}>
            {/* Theme Settings */}
            <Typography variant="h6" fontWeight="bold" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <ThemeIcon color="primary" /> Giao Diện (Theme)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Tùy chỉnh chế độ hiển thị sáng (Light) hoặc tối (Dark)
            </Typography>
            <FormControlLabel
              control={<Switch checked={mode === 'dark'} onChange={toggleColorMode} color="primary" />}
              label={mode === 'dark' ? 'Chế độ Tối (Dark Theme)' : 'Chế độ Sáng (Light Theme)'}
            />

            <Divider sx={{ my: 3 }} />

            {/* Language Settings */}
            <Typography variant="h6" fontWeight="bold" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <LanguageIcon color="primary" /> Ngôn Ngữ (Language)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Chọn ngôn ngữ hiển thị trên giao diện quản trị
            </Typography>
            <FormControl size="small" sx={{ minWidth: 200, mb: 1 }}>
              <Select value={language} onChange={(e) => setLanguage(e.target.value)}>
                <MenuItem value="vi">Tiếng Việt (Vietnamese)</MenuItem>
                <MenuItem value="en">English (US)</MenuItem>
              </Select>
            </FormControl>

            <Divider sx={{ my: 3 }} />

            {/* Notifications */}
            <Typography variant="h6" fontWeight="bold" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <NotificationIcon color="primary" /> Thông Báo (Notifications)
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <FormControlLabel
                control={<Switch checked={emailNotify} onChange={(e) => setEmailNotify(e.target.checked)} />}
                label="Nhận thông báo qua Email khi có học viên mới đăng ký"
              />
              <FormControlLabel
                control={<Switch checked={systemAlert} onChange={(e) => setSystemAlert(e.target.checked)} />}
                label="Bật cảnh báo sự cố hệ thống tự động"
              />
            </Box>

            <Divider sx={{ my: 3 }} />

            <Button variant="contained" onClick={handleSaveSettings} sx={{ px: 4, py: 1, fontWeight: 'bold' }}>
              Lưu Cấu Hình
            </Button>
          </Paper>
        </Grid>
      </Grid>

      <Snackbar open={toast.open} autoHideDuration={4000} onClose={() => setToast({ ...toast, open: false })}>
        <Alert severity={toast.severity}>{toast.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default AdminSettings;
