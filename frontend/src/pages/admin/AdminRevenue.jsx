import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Paper,
  Typography,
  Grid,
  Card,
  CardContent,
  TextField,
  MenuItem,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  CircularProgress,
  Alert,
  TablePagination,
  Divider
} from '@mui/material';
import {
  AttachMoney as RevenueIcon,
  ShoppingBag as OrdersIcon,
  People as StudentsIcon,
  TrendingUp as AovIcon,
  HourglassEmpty as PendingIcon,
  Cancel as FailIcon,
  FilterList as FilterIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';
import api from '../../services/api';

const AdminRevenue = () => {
  const [report, setReport] = useState(null);
  const [ordersPage, setOrdersPage] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [groupBy, setGroupBy] = useState('DAY');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  useEffect(() => {
    fetchDashboardData();
  }, [from, to, groupBy, page, size, statusFilter]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      let revenueUrl = `/api/v1/admin/revenue?groupBy=${groupBy}`;
      if (from) revenueUrl += `&from=${from}`;
      if (to) revenueUrl += `&to=${to}`;

      let ordersUrl = `/api/v1/admin/orders?page=${page}&size=${size}`;
      if (statusFilter && statusFilter !== 'ALL') ordersUrl += `&status=${statusFilter}`;
      if (searchQuery) ordersUrl += `&search=${encodeURIComponent(searchQuery)}`;
      if (from) ordersUrl += `&from=${from}`;
      if (to) ordersUrl += `&to=${to}`;

      const [revenueRes, ordersRes] = await Promise.all([
        api.get(revenueUrl),
        api.get(ordersUrl)
      ]);

      setReport(revenueRes.data?.data);
      setOrdersPage(ordersRes.data?.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải dữ liệu thống kê doanh thu.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchDashboardData();
  };

  const handleResetFilters = () => {
    setFrom('');
    setTo('');
    setGroupBy('DAY');
    setStatusFilter('ALL');
    setSearchQuery('');
    setPage(0);
  };

  const formatVND = (amount) => {
    if (amount === undefined || amount === null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '---';
    return new Date(dateStr).toLocaleString('vi-VN');
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 900, color: 'primary.main', display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <RevenueIcon fontSize="large" /> Thống Kê Doanh Thu & Đơn Hàng (ADMIN)
        </Typography>
        <Button startIcon={<RefreshIcon />} variant="outlined" onClick={fetchDashboardData} sx={{ fontWeight: 700 }}>
          Cập Nhật Dữ Liệu
        </Button>
      </Box>

      {/* FILTER BAR */}
      <Paper elevation={0} sx={{ p: 3, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Box component="form" onSubmit={handleSearchSubmit} sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '2fr 2fr 1.5fr 1.5fr 1fr 1fr' }, gap: 2, alignItems: 'center' }}>
          <TextField
            size="small"
            label="Từ ngày"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={from}
            onChange={(e) => setFrom(e.target.value)}
          />

          <TextField
            size="small"
            label="Đến ngày"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={to}
            onChange={(e) => setTo(e.target.value)}
          />

          <TextField
            size="small"
            select
            label="Gom nhóm"
            value={groupBy}
            onChange={(e) => setGroupBy(e.target.value)}
          >
            <MenuItem value="DAY">Theo Ngày</MenuItem>
            <MenuItem value="MONTH">Theo Tháng</MenuItem>
          </TextField>

          <TextField
            size="small"
            select
            label="Trạng thái đơn"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <MenuItem value="ALL">Tất cả trạng thái</MenuItem>
            <MenuItem value="PAID">Đã thanh toán (PAID)</MenuItem>
            <MenuItem value="PENDING">Đang chờ (PENDING)</MenuItem>
            <MenuItem value="FAILED">Thất bại (FAILED)</MenuItem>
            <MenuItem value="CANCELLED">Đã hủy (CANCELLED)</MenuItem>
            <MenuItem value="EXPIRED">Đã hết hạn (EXPIRED)</MenuItem>
          </TextField>

          <Button variant="contained" color="primary" type="submit" startIcon={<FilterIcon />} sx={{ fontWeight: 700, py: 1 }}>
            Lọc
          </Button>

          <Button variant="outlined" color="inherit" onClick={handleResetFilters} sx={{ fontWeight: 700, py: 1 }}>
            Đặt lại
          </Button>
        </Box>
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {/* KPI OVERVIEW CARDS */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4} lg={2.4}>
          <Card variant="outlined" sx={{ borderRadius: 3, borderLeft: '5px solid #10B981', boxShadow: '0 4px 14px rgba(16,185,129,0.08)' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 800, textTransform: 'uppercase' }}>
                TỔNG DOANH THU
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 900, color: '#10B981', mt: 0.5 }}>
                {formatVND(report?.totalRevenue)}
              </Typography>
              <Typography variant="caption" color="text.secondary">Chỉ tính đơn đã xác minh PAID</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={2.4}>
          <Card variant="outlined" sx={{ borderRadius: 3, borderLeft: '5px solid #4F46E5', boxShadow: '0 4px 14px rgba(79,70,229,0.08)' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 800, textTransform: 'uppercase' }}>
                ĐƠN THÀNH CÔNG
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 900, color: 'primary.main', mt: 0.5 }}>
                {report?.totalPaidOrders || 0} đơn
              </Typography>
              <Typography variant="caption" color="text.secondary">Đơn hàng hoàn tất</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={2.4}>
          <Card variant="outlined" sx={{ borderRadius: 3, borderLeft: '5px solid #3B82F6', boxShadow: '0 4px 14px rgba(59,130,246,0.08)' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 800, textTransform: 'uppercase' }}>
                HỌC VIÊN MUA
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 900, color: '#3B82F6', mt: 0.5 }}>
                {report?.totalStudentsCount || 0} người
              </Typography>
              <Typography variant="caption" color="text.secondary">Học viên đã mua khóa học</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={2.4}>
          <Card variant="outlined" sx={{ borderRadius: 3, borderLeft: '5px solid #8B5CF6', boxShadow: '0 4px 14px rgba(139,92,246,0.08)' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 800, textTransform: 'uppercase' }}>
                GIÁ TRỊ ĐƠN TB (AOV)
              </Typography>
              <Typography variant="h6" sx={{ fontWeight: 900, color: '#8B5CF6', mt: 0.5 }}>
                {formatVND(report?.averageOrderValue)}
              </Typography>
              <Typography variant="caption" color="text.secondary">Trung bình mỗi đơn PAID</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={2.4}>
          <Card variant="outlined" sx={{ borderRadius: 3, borderLeft: '5px solid #F59E0B', boxShadow: '0 4px 14px rgba(245,158,11,0.08)' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 800, textTransform: 'uppercase' }}>
                ĐƠN ĐANG CHỜ (PENDING)
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 900, color: '#F59E0B', mt: 0.5 }}>
                {report?.pendingOrdersCount || 0} đơn
              </Typography>
              <Typography variant="caption" color="text.secondary">Chưa thanh toán</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* TOP COURSES SUMMARY */}
      {report?.topCourses && report.topCourses.length > 0 && (
        <Paper elevation={0} sx={{ p: 3, mb: 4, borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
          <Typography variant="h6" sx={{ fontWeight: 800, mb: 2 }}>
            🏆 Top Khóa Học Mang Lại Doanh Thu Cao Nhất
          </Typography>
          <Grid container spacing={2}>
            {report.topCourses.map((item, idx) => (
              <Grid item xs={12} sm={6} md={4} key={idx}>
                <Card variant="outlined" sx={{ borderRadius: 2.5, borderColor: '#cbd5e1' }}>
                  <CardContent sx={{ p: 2 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 800, color: 'primary.main' }}>
                      #{idx + 1}. {item.courseTitle}
                    </Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                      <Typography variant="body2" color="text.secondary">Lượt mua: <strong>{item.purchaseCount}</strong></Typography>
                      <Typography variant="body2" sx={{ fontWeight: 900, color: '#10B981' }}>{formatVND(item.revenue)}</Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Paper>
      )}

      {/* ORDERS TRANSACTIONS TABLE */}
      <Paper elevation={0} sx={{ borderRadius: 3, border: '1px solid #e2e8f0', bgcolor: 'white' }}>
        <Box sx={{ p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" sx={{ fontWeight: 800 }}>
            📋 Danh Sách Giao Dịch Đơn Hàng ({ordersPage?.totalElements || 0} đơn)
          </Typography>
        </Box>
        <Divider />

        <TableContainer>
          <Table>
            <TableHead sx={{ bgcolor: '#f8fafc' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 800 }}>Mã Đơn Hàng</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Người Mua (Email)</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Khóa Học</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Tổng Tiền</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Cổng Thanh Toán</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Trạng Thái</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>Thời Gian</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                    <CircularProgress size={30} />
                  </TableCell>
                </TableRow>
              ) : ordersPage?.items?.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    Không có đơn hàng nào khớp với bộ lọc.
                  </TableCell>
                </TableRow>
              ) : (
                ordersPage?.items?.map((order) => (
                  <TableRow key={order.id} hover>
                    <TableCell sx={{ fontWeight: 800, color: 'primary.main' }}>
                      {order.orderCode}
                    </TableCell>
                    <TableCell sx={{ fontWeight: 600 }}>
                      {order.userEmailSnapshot}
                    </TableCell>
                    <TableCell sx={{ fontWeight: 600 }}>
                      {order.courseTitleSnapshot}
                    </TableCell>
                    <TableCell sx={{ fontWeight: 900, color: '#10B981' }}>
                      {formatVND(order.totalAmount)}
                    </TableCell>
                    <TableCell>
                      <Chip label={order.paymentProvider} size="small" variant="outlined" />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={order.status}
                        color={order.status === 'PAID' ? 'success' : order.status === 'PENDING' ? 'warning' : 'error'}
                        size="small"
                        sx={{ fontWeight: 800 }}
                      />
                    </TableCell>
                    <TableCell sx={{ fontSize: '0.85rem' }}>
                      {formatDate(order.paidAt || order.createdAt)}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <TablePagination
          rowsPerPageOptions={[10, 20, 50]}
          component="div"
          count={ordersPage?.totalElements || 0}
          rowsPerPage={size}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          onRowsPerPageChange={(e) => {
            setSize(parseInt(e.target.value, 10));
            setPage(0);
          }}
        />
      </Paper>
    </Container>
  );
};

export default AdminRevenue;
