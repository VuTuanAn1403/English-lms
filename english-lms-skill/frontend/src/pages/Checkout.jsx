import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Paper,
  Typography,
  Button,
  RadioGroup,
  FormControlLabel,
  Radio,
  Divider,
  Alert,
  CircularProgress,
  Card,
  CardMedia,
  CardContent,
  Chip
} from '@mui/material';
import {
  ShoppingCart as CartIcon,
  CreditCard as CardIcon,
  Lock as LockIcon,
  CheckCircle as CheckIcon,
  ArrowBack as BackIcon,
  LocalOffer as OfferIcon
} from '@mui/icons-material';
import api from '../services/api';

const Checkout = () => {
  const { courseId } = useParams();
  const navigate = useNavigate();

  const [course, setCourse] = useState(null);
  const [access, setAccess] = useState(null);
  const [provider, setProvider] = useState('VNPAY');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCourseDetails();
  }, [courseId]);

  const fetchCourseDetails = async () => {
    setLoading(true);
    setError(null);
    try {
      const [courseRes, accessRes] = await Promise.all([
        api.get(`/api/v1/courses/${courseId}`),
        api.get(`/api/v1/courses/${courseId}/access`)
      ]);
      const courseData = courseRes.data?.data;
      const accessData = accessRes.data?.data;

      setCourse(courseData);
      setAccess(accessData);

      if (accessData?.hasFullAccess) {
        // User already owns the course
        navigate(`/courses/${courseId}`);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải thông tin khóa học.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOrderAndPay = async () => {
    setSubmitting(true);
    setError(null);
    try {
      // 1. Create Order
      const orderRes = await api.post('/api/v1/orders', {
        courseId,
        paymentProvider: provider
      });
      const orderData = orderRes.data?.data;

      if (!orderData?.orderCode) {
        throw new Error('Không thể tạo đơn hàng. Vui lòng thử lại.');
      }

      // 2. Create Payment URL
      const payRes = await api.post(`/api/v1/payments/${orderData.orderCode}/create?returnUrl=${encodeURIComponent(window.location.origin + '/payment/result')}`);
      const paymentUrl = payRes.data?.data?.paymentUrl;

      if (paymentUrl) {
        window.location.href = paymentUrl;
      } else {
        throw new Error('Không nhận được liên kết thanh toán.');
      }
    } catch (err) {
      const status = err.response?.status;
      const msg = err.response?.data?.message;
      if (status === 409) {
        setError('Bạn đã sở hữu khóa học này rồi!');
      } else {
        setError(msg || err.message || 'Đã có lỗi xảy ra khi tạo đơn hàng thanh toán.');
      }
      setSubmitting(false);
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

  if (error && !course) {
    return (
      <Container maxWidth="md" sx={{ py: 6 }}>
        <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>
        <Button startIcon={<BackIcon />} onClick={() => navigate(-1)}>
          Quay lại
        </Button>
      </Container>
    );
  }

  const originalPrice = course?.price || 0;
  const effectivePrice = course?.effectivePrice || 0;
  const discount = Math.max(0, originalPrice - effectivePrice);

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Button startIcon={<BackIcon />} onClick={() => navigate(-1)} sx={{ mb: 3, fontWeight: 700 }}>
        Trở về chi tiết khóa học
      </Button>

      <Typography variant="h4" sx={{ fontWeight: 800, mb: 1, color: 'primary.main', display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <CartIcon fontSize="large" /> Thanh Toán Mua Khóa Học
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Hoàn tất thanh toán để mở khóa toàn bộ tất cả các bài học trong khóa học.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3, borderRadius: 2, fontWeight: 600 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '7fr 5fr' }, gap: 4 }}>
        {/* LEFT COLUMN: COURSE SUMMARY & PAYMENT METHODS */}
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* COURSE CARD SUMMARY */}
          <Card variant="outlined" sx={{ borderRadius: 3, borderColor: '#e2e8f0', display: 'flex', flexDirection: { xs: 'column', sm: 'row' } }}>
            <CardMedia
              component="img"
              image={course?.imageUrl || 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d'}
              alt={course?.title}
              sx={{ width: { xs: '100%', sm: 200 }, height: { xs: 160, sm: 'auto' }, objectFit: 'cover' }}
            />
            <CardContent sx={{ p: 3, flex: 1 }}>
              <Chip label={course?.level || 'Tổng hợp'} color="primary" size="small" sx={{ fontWeight: 800, mb: 1 }} />
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                {course?.title}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {course?.description}
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>
                Bao gồm: Toàn bộ {course?.totalLessons || 0} bài học bài bản + Hỗ trợ hỏi đáp AI Teacher 24/7.
              </Typography>
            </CardContent>
          </Card>

          {/* PAYMENT METHOD SELECTION */}
          <Paper elevation={0} sx={{ p: 3, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
              <CardIcon color="primary" /> Chọn Cổng Thanh Toán
            </Typography>

            <RadioGroup value={provider} onChange={(e) => setProvider(e.target.value)}>
              <Paper
                variant="outlined"
                onClick={() => setProvider('VNPAY')}
                sx={{
                  p: 2,
                  mb: 1.5,
                  borderRadius: 2,
                  borderColor: provider === 'VNPAY' ? 'primary.main' : '#e2e8f0',
                  bgcolor: provider === 'VNPAY' ? 'rgba(79,70,229,0.04)' : 'transparent',
                  cursor: 'pointer'
                }}
              >
                <FormControlLabel
                  value="VNPAY"
                  control={<Radio color="primary" />}
                  label={
                    <Box sx={{ ml: 1 }}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'primary.main' }}>
                        Cổng VNPay Sandbox (Ngân Hàng / QR Code)
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Thanh toán bảo mật qua Thẻ ATM, Ví VNPay, QR Pay hoặc Thẻ Quốc Tế (Môi trường Test Sandbox).
                      </Typography>
                    </Box>
                  }
                />
              </Paper>

              <Paper
                variant="outlined"
                onClick={() => setProvider('MOCK')}
                sx={{
                  p: 2,
                  borderRadius: 2,
                  borderColor: provider === 'MOCK' ? 'primary.main' : '#e2e8f0',
                  bgcolor: provider === 'MOCK' ? 'rgba(79,70,229,0.04)' : 'transparent',
                  cursor: 'pointer'
                }}
              >
                <FormControlLabel
                  value="MOCK"
                  control={<Radio color="primary" />}
                  label={
                    <Box sx={{ ml: 1 }}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'secondary.main' }}>
                        Thanh Toán Mô Phỏng (MOCK Payment - Dành Cho Demo)
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Dành cho buổi báo cáo/chấm điểm khi không kết nối mạng tới VNPay.
                      </Typography>
                    </Box>
                  }
                />
              </Paper>
            </RadioGroup>
          </Paper>
        </Box>

        {/* RIGHT COLUMN: ORDER PRICE SUMMARY */}
        <Box>
          <Paper elevation={0} sx={{ p: 3.5, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white', position: 'sticky', top: 20 }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 2.5 }}>
              Tóm Tắt Đơn Hàng
            </Typography>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
              <Typography color="text.secondary">Giá gốc:</Typography>
              <Typography sx={{ textDecoration: discount > 0 ? 'line-through' : 'none', color: discount > 0 ? 'text.secondary' : 'text.primary', fontWeight: 600 }}>
                {formatVND(originalPrice)}
              </Typography>
            </Box>

            {discount > 0 && (
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography color="secondary.main" sx={{ fontWeight: 700, display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <OfferIcon fontSize="small" /> Ưu đãi giảm giá:
                </Typography>
                <Typography color="secondary.main" sx={{ fontWeight: 800 }}>
                  -{formatVND(discount)}
                </Typography>
              </Box>
            )}

            <Divider sx={{ my: 2 }} />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', mb: 3 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
                Tổng thanh toán:
              </Typography>
              <Typography variant="h4" sx={{ fontWeight: 900, color: 'primary.main' }}>
                {formatVND(effectivePrice)}
              </Typography>
            </Box>

            <Button
              fullWidth
              variant="contained"
              size="large"
              color="primary"
              onClick={handleCreateOrderAndPay}
              disabled={submitting}
              startIcon={submitting ? <CircularProgress size={20} color="inherit" /> : <LockIcon />}
              sx={{ py: 1.8, borderRadius: 2.5, fontWeight: 800, fontSize: '1.05rem', textTransform: 'none', boxShadow: '0 4px 14px rgba(79,70,229,0.3)' }}
            >
              {submitting ? 'Đang kết nối cổng thanh toán...' : 'Xác Nhận Thanh Toán'}
            </Button>

            <Box sx={{ mt: 2.5, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, color: 'text.secondary' }}>
              <CheckIcon color="success" fontSize="small" />
              <Typography variant="caption" sx={{ fontWeight: 600 }}>
                Thanh toán an toàn 100% & Kích hoạt khóa học tức thì.
              </Typography>
            </Box>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default Checkout;
