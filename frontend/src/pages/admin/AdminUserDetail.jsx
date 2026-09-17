import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Avatar,
  Button,
  Grid,
  Chip,
  Divider,
} from '@mui/material';
import { ArrowBack as ArrowBackIcon, Email as EmailIcon, Badge as BadgeIcon } from '@mui/icons-material';

const AdminUserDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/admin/users')} sx={{ mb: 3 }}>
        Quay lại Danh sách Người dùng
      </Button>

      <Paper sx={{ p: 4, borderRadius: 4 }}>
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} sm={3} sx={{ textAlign: 'center' }}>
            <Avatar
              src="https://images.unsplash.com/photo-1534528741775-53994a69daeb"
              sx={{ width: 120, height: 120, margin: '0 auto', mb: 2 }}
            />
            <Chip label="ADMIN" color="error" fontWeight="bold" />
          </Grid>

          <Grid item xs={12} sm={9}>
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              Thông Tin Chi Tiết Người Dùng
            </Typography>
            <Typography color="text.secondary" gutterBottom>
              Mã hệ thống (ID): {id}
            </Typography>
            <Divider sx={{ my: 2 }} />

            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="caption" color="text.secondary" display="block">
                  HỌ VÀ TÊN
                </Typography>
                <Typography variant="subtitle1" fontWeight="bold">
                  Admin Root
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <Typography variant="caption" color="text.secondary" display="block">
                  EMAIL
                </Typography>
                <Typography variant="subtitle1" fontWeight="bold">
                  admin@gmail.com
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <Typography variant="caption" color="text.secondary" display="block">
                  TRẠNG THÁI TÀI KHOẢN
                </Typography>
                <Chip label="Đang hoạt động" color="success" size="small" sx={{ mt: 0.5 }} />
              </Grid>

              <Grid item xs={12} sm={6}>
                <Typography variant="caption" color="text.secondary" display="block">
                  NGÀY THAM GIA
                </Typography>
                <Typography variant="subtitle1" fontWeight="bold">
                  24/07/2026
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

export default AdminUserDetail;
