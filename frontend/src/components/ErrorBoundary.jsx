import React from 'react';
import { Box, Container, Typography, Button, Paper } from '@mui/material';
import { Refresh as RefreshIcon, Home as HomeIcon } from '@mui/icons-material';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Unhandled React error captured by ErrorBoundary:', error, errorInfo);
  }

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/';
  };

  render() {
    if (this.state.hasError) {
      return (
        <Container maxWidth="sm" sx={{ py: 10 }}>
          <Paper
            elevation={3}
            sx={{
              p: 5,
              textAlign: 'center',
              borderRadius: 4,
              border: '1px solid #fee2e2',
              bgcolor: '#fff'
            }}
          >
            <Typography variant="h4" sx={{ fontWeight: 800, color: 'error.main', mb: 2 }}>
              Đã xảy ra sự cố không mong muốn
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 4, lineHeight: 1.6 }}>
              Hệ thống đã ghi nhận lỗi giao diện. Vui lòng tải lại trang hoặc quay về trang chủ để tiếp tục học tập.
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
              <Button
                variant="contained"
                color="primary"
                startIcon={<RefreshIcon />}
                onClick={this.handleReload}
                sx={{ borderRadius: 2, px: 3, fontWeight: 700 }}
              >
                Tải lại trang
              </Button>
              <Button
                variant="outlined"
                color="inherit"
                startIcon={<HomeIcon />}
                onClick={this.handleGoHome}
                sx={{ borderRadius: 2, px: 3, fontWeight: 700 }}
              >
                Về trang chủ
              </Button>
            </Box>
          </Paper>
        </Container>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
