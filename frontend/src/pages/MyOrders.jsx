import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  CircularProgress,
  Alert,
  TablePagination
} from '@mui/material';
import {
  ReceiptLong as OrderIcon,
  PlayCircleOutline as LearnIcon,
  Payment as PayIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';
import api from '../services/api';

const MyOrders = () => {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchOrders();
  }, [page, size]);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get(`/api/v1/orders/my?page=${page}&size=${size}`);
      const data = res.data?.data;
      setOrders(data?.items || []);
      setTotalElements(data?.totalElements || 0);
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải danh sách đơn hàng.');
    } finally {
      setLoading(false);
    }
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '---';
    return new Date(dateStr).toLocaleString('vi-VN');
  };

  const getStatusChip = (status) => {
    switch (status) {
      case 'PAID':
        return <Chip label="Đã Thanh Toán" color="success" size="small" sx={{ fontWeight: 800 }} />;
      case 'PENDING':
        return <Chip label="Đang Chờ Thanh Toán" color="warning" size="small" sx={{ fontWeight: 800 }} />;
      case 'FAILED':
        return <Chip label="Thất Bại" color="error" size="small" sx={{ fontWeight: 800 }} />;
      case 'EXPIRED':
        return <Chip label="Đã Hết Hạn" color="default" size="small" sx={{ fontWeight: 800 }} />;
      case 'CANCELLED':
        return <Chip label="Đã Hủy" color="default" size="small" sx={{ fontWeight: 800 }} />;
      default:
        return <Chip label={status} size="small" />;
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, color: 'primary.main', display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <OrderIcon fontSize="large" /> Đơn Hàng Của Tôi
        </Typography>
        <Button startIcon={<RefreshIcon />} onClick={fetchOrders} variant="outlined" size="small" sx={{ fontWeight: 700 }}>
          Làm mới
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      <TableContainer component={Paper} elevation={0} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <Table>
          <TableHead sx={{ bgcolor: '#f8fafc' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 800 }}>Mã Đơn Hàng</TableCell>
              <TableCell sx={{ fontWeight: 800 }}>Khóa Học</TableCell>
              <TableCell sx={{ fontWeight: 800 }}>Số Tiền</TableCell>
              <TableCell sx={{ fontWeight: 800 }}>Cổng Thanh Toán</TableCell>
              <TableCell sx={{ fontWeight: 800 }}>Trạng Thái</TableCell>
              <TableCell sx={{ fontWeight: 800 }}>Ngày Tạo</TableCell>
              <TableCell sx={{ fontWeight: 800 }} align="center">Thao Tác</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                  <CircularProgress size={30} />
                </TableCell>
              </TableRow>
            ) : orders.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  Bạn chưa có đơn hàng mua khóa học nào.
                </TableCell>
              </TableRow>
            ) : (
              orders.map((order) => (
                <TableRow key={order.id} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'primary.main' }}>
                    {order.orderCode}
                  </TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>
                    {order.courseTitleSnapshot}
                  </TableCell>
                  <TableCell sx={{ fontWeight: 800, color: '#10B981' }}>
                    {formatVND(order.totalAmount)}
                  </TableCell>
                  <TableCell>
                    <Chip label={order.paymentProvider} size="small" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    {getStatusChip(order.status)}
                  </TableCell>
                  <TableCell sx={{ fontSize: '0.85rem' }}>
                    {formatDate(order.createdAt)}
                  </TableCell>
                  <TableCell align="center">
                    {order.status === 'PAID' ? (
                      <Button
                        size="small"
                        variant="contained"
                        color="success"
                        startIcon={<LearnIcon />}
                        onClick={() => navigate(`/courses/${order.courseId}`)}
                        sx={{ borderRadius: 2, fontWeight: 700 }}
                      >
                        Vào Học
                      </Button>
                    ) : order.status === 'PENDING' ? (
                      <Button
                        size="small"
                        variant="contained"
                        color="primary"
                        startIcon={<PayIcon />}
                        onClick={() => navigate(`/checkout/${order.courseId}`)}
                        sx={{ borderRadius: 2, fontWeight: 700 }}
                      >
                        Thanh Toán
                      </Button>
                    ) : (
                      <Typography variant="caption" color="text.secondary">---</Typography>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={totalElements}
          rowsPerPage={size}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          onRowsPerPageChange={(e) => {
            setSize(parseInt(e.target.value, 10));
            setPage(0);
          }}
        />
      </TableContainer>
    </Container>
  );
};

export default MyOrders;
