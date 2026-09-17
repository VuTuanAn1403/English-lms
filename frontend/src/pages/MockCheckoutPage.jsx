import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Paper,
  Typography,
  Button,
  Alert,
  CircularProgress,
  Divider,
  Chip
} from '@mui/material';
import {
  CheckCircle as SuccessIcon,
  Cancel as FailIcon,
  AccountBalanceWallet as WalletIcon,
  Info as InfoIcon
} from '@mui/icons-material';
import api from '../services/api';

const MockCheckoutPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const orderCode = searchParams.get('orderCode');

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (orderCode) {
      fetchOrder();
    } else {
      setError('Mã đơn hàng không hợp lệ.');
      setLoading(false);
    }
  }, [orderCode]);

  const fetchOrder = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/api/v1/orders/${orderCode}`);
      setOrder(res.data?.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải thông tin đơn hàng.');
    } finally {
      setLoading(false);
    }
  };

  const handleProcessMockPayment = async (status) => {
    setProcessing(true);
    setError(null);
    try {
      await api.post('/api/v1/payments/mock/process', {
        orderCode,
        status
      });
      navigate(`/payment/result?orderCode=${orderCode}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Đã có lỗi xảy ra khi xử lý thanh toán.');
      setProcessing(false);
    }
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress size={40} color="primary" />
      </Box>
    );
  }

  return (
    <Container maxWidth="sm" sx={{ py: 6 }}>
      <Paper elevation={0} sx={{ p: 4, borderRadius: 4, border: '2px solid #3b82f6', bgcolor: 'white', textAlign: 'center' }}>
        <Chip icon={<WalletIcon />} label="MOCK PAYMENT PROVIDER" color="primary" sx={{ fontWeight: 800, mb: 2 }} />

        <Alert severity="warning" icon={<InfoIcon />} sx={{ mb: 3, textAlign: 'left', borderRadius: 2, fontWeight: 600 }}>
          <strong>CỔNG THANH TOÁN MÔ PHỎNG (MOCK PAYMENT)</strong><br />
          Tính năng được bật trong môi trường Local/Demo để hỗ trợ kiểm thử và trình diễn báo cáo mà không cần tài khoản VNPay Sandbox thật. Không có giao dịch tiền thật phát sinh.
        </Alert>

        {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

        {order && (
          <Box sx={{ my: 3, p: 2.5, bgcolor: '#f8fafc', borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <Typography variant="body2" color="text.secondary">Mã Đơn Hàng:</Typography>
            <Typography variant="h6" sx={{ fontWeight: 800, color: 'primary.main', mb: 1 }}>
              {order.orderCode}
            </Typography>

            <Typography variant="body2" color="text.secondary">Khóa Học:</Typography>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1 }}>
              {order.courseTitleSnapshot}
            </Typography>

            <Divider sx={{ my: 1.5 }} />

            <Typography variant="body2" color="text.secondary">Số Tiền Thanh Toán:</Typography>
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#10B981' }}>
              {formatVND(order.totalAmount)}
            </Typography>
          </Box>
        )}

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 3 }}>
          <Button
            variant="contained"
            color="success"
            size="large"
            onClick={() => handleProcessMockPayment('SUCCESS')}
            disabled={processing}
            startIcon={processing ? <CircularProgress size={20} color="inherit" /> : <SuccessIcon />}
            sx={{ py: 1.5, borderRadius: 2.5, fontWeight: 800, fontSize: '1rem' }}
          >
            Thanh Toán Thành Công (Simulate Success)
          </Button>

          <Button
            variant="outlined"
            color="error"
            size="large"
            onClick={() => handleProcessMockPayment('FAILED')}
            disabled={processing}
            startIcon={<FailIcon />}
            sx={{ py: 1.5, borderRadius: 2.5, fontWeight: 800 }}
          >
            Thanh Toán Thất Bại (Simulate Fail)
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default MockCheckoutPage;
