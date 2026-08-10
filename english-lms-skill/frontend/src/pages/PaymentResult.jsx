import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Paper,
  Typography,
  Button,
  CircularProgress,
  Alert,
  Divider,
  Chip
} from '@mui/material';
import {
  CheckCircle as SuccessIcon,
  ErrorOutline as ErrorIcon,
  School as CourseIcon,
  ReceiptLong as OrderIcon,
  Home as HomeIcon
} from '@mui/icons-material';
import api from '../services/api';

const PaymentResult = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const orderCode = searchParams.get('orderCode') || searchParams.get('vnp_TxnRef');
  const vnpResponseCode = searchParams.get('vnp_ResponseCode');

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    handleVerifyPayment();
  }, []);

  const handleVerifyPayment = async () => {
    setLoading(true);
    setError(null);
    try {
      // If returning from VNPay callback via browser URL params, call backend callback endpoint to process
      if (searchParams.has('vnp_SecureHash')) {
        const queryStr = searchParams.toString();
        const callbackRes = await api.get(`/api/v1/payments/vnpay/callback?${queryStr}`);
        setOrder(callbackRes.data?.data);
      } else if (orderCode) {
        // Query backend for real order status
        const orderRes = await api.get(`/api/v1/orders/${orderCode}`);
        setOrder(orderRes.data?.data);
      } else {
        setError('Không tìm thấy thông tin mã đơn hàng.');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể xác thực trạng thái thanh toán.');
    } finally {
      setLoading(false);
    }
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', minHeight: '60vh', gap: 2 }}>
        <CircularProgress size={50} color="primary" />
        <Typography variant="h6" color="text.secondary" sx={{ fontWeight: 600 }}>
          Đang xác thực kết quả thanh toán từ hệ thống...
        </Typography>
      </Box>
    );
  }

  const isSuccess = order?.status === 'PAID';
  const isPending = order?.status === 'PENDING';
  const isFailed = order?.status === 'FAILED' || order?.status === 'EXPIRED' || order?.status === 'CANCELLED';

  return (
    <Container maxWidth="sm" sx={{ py: 6 }}>
      <Paper elevation={0} sx={{ p: 4, borderRadius: 4, border: '1px solid #e2e8f0', bgcolor: 'white', textAlign: 'center' }}>
        {isSuccess && (
          <>
            <SuccessIcon sx={{ fontSize: 72, color: '#10B981', mb: 2 }} />
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#10B981', mb: 1 }}>
              Thanh Toán Thành Công!
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Chúc mừng bạn! Khóa học đã được mở khóa hoàn toàn.
            </Typography>
          </>
        )}

        {isFailed && (
          <>
            <ErrorIcon sx={{ fontSize: 72, color: '#EF4444', mb: 2 }} />
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#EF4444', mb: 1 }}>
              Thanh Toán Thất Bại
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Giao dịch của bạn chưa thể hoàn tất. Vui lòng kiểm tra lại tài khoản hoặc thử lại.
            </Typography>
          </>
        )}

        {isPending && (
          <>
            <CircularProgress size={60} color="warning" sx={{ mb: 2 }} />
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'warning.main', mb: 1 }}>
              Đang Chờ Xử Lý
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Hệ thống đang tiếp nhận xác nhận từ cổng thanh toán.
            </Typography>
          </>
        )}

        {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

        {order && (
          <Box sx={{ p: 3, bgcolor: '#f8fafc', borderRadius: 3, border: '1px solid #e2e8f0', mb: 4, textAlign: 'left' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
              <Typography color="text.secondary">Mã đơn hàng:</Typography>
              <Typography sx={{ fontWeight: 800 }}>{order.orderCode}</Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
              <Typography color="text.secondary">Khóa học:</Typography>
              <Typography sx={{ fontWeight: 700, color: 'primary.main' }}>{order.courseTitleSnapshot}</Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
              <Typography color="text.secondary">Số tiền thanh toán:</Typography>
              <Typography sx={{ fontWeight: 900, color: '#10B981' }}>{formatVND(order.totalAmount)}</Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
              <Typography color="text.secondary">Phương thức:</Typography>
              <Chip label={order.paymentProvider} size="small" color="primary" sx={{ fontWeight: 800 }} />
            </Box>

            <Divider sx={{ my: 1.5 }} />

            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography color="text.secondary">Trạng thái:</Typography>
              <Chip
                label={order.status === 'PAID' ? 'Đã Thanh Toán' : order.status}
                color={order.status === 'PAID' ? 'success' : 'error'}
                sx={{ fontWeight: 800 }}
              />
            </Box>
          </Box>
        )}

        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
          {isSuccess && order?.courseId && (
            <Button
              variant="contained"
              color="primary"
              size="large"
              startIcon={<CourseIcon />}
              onClick={() => navigate(`/courses/${order.courseId}`)}
              sx={{ borderRadius: 2.5, fontWeight: 800, px: 3 }}
            >
              Bắt Đầu Học Ngay
            </Button>
          )}

          <Button
            variant="outlined"
            size="large"
            startIcon={<OrderIcon />}
            onClick={() => navigate('/my-orders')}
            sx={{ borderRadius: 2.5, fontWeight: 700 }}
          >
            Đơn Hàng Của Tôi
          </Button>

          <Button
            variant="text"
            size="large"
            startIcon={<HomeIcon />}
            onClick={() => navigate('/')}
          >
            Trang Chủ
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default PaymentResult;
