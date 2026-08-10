import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TextField,
  InputAdornment,
  Tabs,
  Tab,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Tooltip,
  CircularProgress,
  Divider,
} from '@mui/material';
import {
  Search as SearchIcon,
  Visibility as ViewIcon,
  Psychology as AiIcon,
  CheckCircle as GrammarIcon,
  Quiz as QuizIcon,
  Chat as ChatIcon,
} from '@mui/icons-material';
import api from '../../services/api';

const AdminAi = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentTab, setCurrentTab] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  const [selectedLog, setSelectedLog] = useState(null);
  const [detailOpen, setDetailOpen] = useState(false);

  useEffect(() => {
    fetchAiHistory();
  }, []);

  const fetchAiHistory = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/v1/ai/history');
      const items = Array.isArray(res.data?.data)
        ? res.data.data
        : (res.data?.data?.items || []);
      setHistory(items);
    } catch (err) {
      console.error('Error fetching AI history:', err);
      setHistory([]);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
    setPage(0);
  };

  const filteredLogs = history.filter((item) => {
    const promptText = item.userPrompt || item.prompt || '';
    const responseText = item.aiResponse || item.response || '';
    const typeValue = item.promptType || item.type || '';

    const promptMatch = promptText.toLowerCase().includes(searchTerm.toLowerCase());
    const responseMatch = responseText.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesSearch = promptMatch || responseMatch;

    const matchesTab = currentTab === 'ALL' || typeValue === currentTab;
    return matchesSearch && matchesTab;
  });

  const getChipColor = (type) => {
    switch (type) {
      case 'GRAMMAR':
        return 'warning';
      case 'QUIZ':
        return 'success';
      default:
        return 'primary';
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Quản Lý Lịch Sử AI Assistant
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Xem lịch sử hỏi đáp Chatbot, Kiểm tra Ngữ pháp và Sinh câu hỏi Trắc nghiệm của người dùng
          </Typography>
        </Box>
      </Box>

      {/* Tabs Filter */}
      <Paper sx={{ mb: 3, borderRadius: 3 }}>
        <Tabs
          value={currentTab}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="scrollable"
        >
          <Tab icon={<AiIcon />} iconPosition="start" label="Tất cả Lịch sử" value="ALL" />
          <Tab icon={<ChatIcon />} iconPosition="start" label="AI Chat" value="CHAT" />
          <Tab icon={<GrammarIcon />} iconPosition="start" label="Grammar Checker" value="GRAMMAR" />
          <Tab icon={<QuizIcon />} iconPosition="start" label="Quiz Generator" value="QUIZ" />
        </Tabs>
      </Paper>

      {/* Search Bar */}
      <Paper sx={{ p: 2, mb: 3, borderRadius: 3 }}>
        <TextField
          size="small"
          fullWidth
          placeholder="Tìm theo nội dung câu hỏi hoặc phản hồi của AI..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />
      </Paper>

      {/* Table */}
      <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Table>
            <TableHead sx={{ bgcolor: 'action.hover' }}>
              <TableRow>
                <TableCell fontWeight="bold">Loại Tương Tác</TableCell>
                <TableCell fontWeight="bold">Câu Hỏi / Yêu Cầu (Prompt)</TableCell>
                <TableCell fontWeight="bold">Phản Hồi Từ AI (Response)</TableCell>
                <TableCell fontWeight="bold">Thời Gian</TableCell>
                <TableCell fontWeight="bold" align="right">Thao Tác</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredLogs.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                    Chưa có lịch sử tương tác AI nào.
                  </TableCell>
                </TableRow>
              ) : (
                filteredLogs
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((log) => {
                    const displayType = log.promptType || log.type || 'CHAT';
                    const displayPrompt = log.userPrompt || log.prompt || '';
                    const displayResponse = log.aiResponse || log.response || '';

                    return (
                      <TableRow key={log.id || Math.random()} hover>
                        <TableCell>
                          <Chip
                            label={displayType}
                            size="small"
                            color={getChipColor(displayType)}
                            fontWeight="bold"
                          />
                        </TableCell>
                        <TableCell sx={{ maxWidth: 260 }}>
                          <Typography fontWeight="bold" noWrap>
                            {displayPrompt}
                          </Typography>
                        </TableCell>
                        <TableCell sx={{ maxWidth: 320 }}>
                          <Typography variant="body2" color="text.secondary" noWrap>
                            {displayResponse}
                          </Typography>
                        </TableCell>
                        <TableCell sx={{ minWidth: 140 }}>
                          {new Date(log.createdAt || Date.now()).toLocaleString('vi-VN')}
                        </TableCell>
                        <TableCell align="right">
                          <Tooltip title="Xem Chi Tiết Tương Tác">
                            <IconButton
                              color="primary"
                              onClick={() => {
                                setSelectedLog(log);
                                setDetailOpen(true);
                              }}
                            >
                              <ViewIcon />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    );
                  })
              )}
            </TableBody>
          </Table>
        )}
        <TablePagination
          component="div"
          count={filteredLogs.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
        />
      </TableContainer>

      {/* Details Dialog */}
      <Dialog open={detailOpen} onClose={() => setDetailOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle fontWeight="bold">
          Chi Tiết Lượt Tương Tác AI ({selectedLog?.promptType || selectedLog?.type})
        </DialogTitle>
        <DialogContent dividers>
          <Box sx={{ mb: 3 }}>
            <Typography variant="subtitle2" color="primary" fontWeight="bold" gutterBottom>
              CÂU HỎI / PROMPT CỦA HỌC VIÊN:
            </Typography>
            <Paper variant="outlined" sx={{ p: 2, bgcolor: 'action.hover', borderRadius: 2 }}>
              <Typography fontWeight="500">
                {selectedLog?.userPrompt || selectedLog?.prompt}
              </Typography>
            </Paper>
          </Box>

          <Divider sx={{ my: 2 }} />

          <Box>
            <Typography variant="subtitle2" color="secondary" fontWeight="bold" gutterBottom>
              CÂU TRẢ LỜI TỪ TRỢ LÝ AI:
            </Typography>
            <Paper variant="outlined" sx={{ p: 2, borderRadius: 2 }}>
              <Typography whiteSpace="pre-line">
                {selectedLog?.aiResponse || selectedLog?.response}
              </Typography>
            </Paper>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailOpen(false)}>Đóng</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdminAi;
