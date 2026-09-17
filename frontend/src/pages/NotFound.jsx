import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Home as HomeIcon } from '@mui/icons-material';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth="md" sx={{ py: 12, textAlign: 'center' }} className="animate-fade-in">
      <Typography variant="h1" color="primary" sx={{ fontSize: '8rem', fontWeight: 800, lineHeight: 1 }}>
        404
      </Typography>
      <Typography variant="h4" sx={{ fontWeight: 800, mt: 2, mb: 1 }}>
        Không Tìm Thấy Trang
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Trang bạn đang tìm kiếm không tồn tại hoặc đã được di chuyển.
      </Typography>
      <Button
        variant="contained"
        color="primary"
        size="large"
        startIcon={<HomeIcon />}
        onClick={() => navigate('/')}
        sx={{ fontWeight: 700, px: 4, py: 1.5 }}
      >
        Trở về Trang chủ
      </Button>
    </Container>
  );
};

export default NotFound;
