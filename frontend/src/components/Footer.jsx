import React from 'react';
import { Box, Container, Typography, Grid, Link, Divider } from '@mui/material';
import { School as SchoolIcon, SmartToy as AiIcon, Security as SecurityIcon } from '@mui/icons-material';

const Footer = () => {
  return (
    <Box sx={{ bgcolor: '#0F172A', color: '#94A3B8', pt: 6, pb: 4, mt: 'auto', borderTop: '1px solid #1E293B' }}>
      <Container maxWidth="lg">
        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Box sx={{ p: 1, bgcolor: 'primary.main', borderRadius: 2, display: 'flex', mr: 1.5, color: 'white' }}>
                <SchoolIcon fontSize="medium" />
              </Box>
              <Typography variant="h6" color="white" sx={{ fontWeight: 800 }}>
                English<Typography component="span" variant="h6" color="primary.light" sx={{ fontWeight: 800 }}>LMS</Typography>
              </Typography>
            </Box>
            <Typography variant="body2" sx={{ mb: 2, lineHeight: 1.7 }}>
              Hệ thống quản lý khóa học tiếng Anh trực tuyến tích hợp trí tuệ nhân tạo (Microservice Architecture & Google Gemini AI).
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="subtitle1" color="white" sx={{ fontWeight: 700, mb: 2 }}>
              Liên kết nhanh
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Link href="/" color="inherit" underline="hover">Trang chủ</Link>
              <Link href="/courses" color="inherit" underline="hover">Danh sách khóa học</Link>
              <Link href="/ai-assistant" color="inherit" underline="hover">Trợ lý học tập AI</Link>
              <Link href="/profile" color="inherit" underline="hover">Hồ sơ cá nhân</Link>
            </Box>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="subtitle1" color="white" sx={{ fontWeight: 700, mb: 2 }}>
              Tính năng nổi bật
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <AiIcon color="primary" fontSize="small" />
                <Typography variant="body2">Hỏi đáp AI chuẩn ngữ cảnh</Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <AiIcon color="secondary" fontSize="small" />
                <Typography variant="body2">Sửa lỗi ngữ pháp & giải thích chi tiết</Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <SecurityIcon color="warning" fontSize="small" />
                <Typography variant="body2">Bảo mật JWT & Microservice Gateway</Typography>
              </Box>
            </Box>
          </Grid>
        </Grid>

        <Divider sx={{ my: 4, borderColor: '#334155' }} />

        <Typography variant="body2" align="center" color="#64748B">
          © {new Date().getFullYear()} English LMS. Tất cả quyền được bảo lưu.
        </Typography>
      </Container>
    </Box>
  );
};

export default Footer;
