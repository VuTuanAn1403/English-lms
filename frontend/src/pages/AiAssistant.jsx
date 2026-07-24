import React from 'react';
import { Container, Typography, Box } from '@mui/material';
import ChatBox from '../components/ChatBox';

const AiAssistant = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 6 }} className="animate-fade-in">
      <Box sx={{ mb: 4, textCenter: 'center' }}>
        <Typography variant="h3" sx={{ fontWeight: 800, mb: 1 }}>
          Trợ Lý AI Học Tiếng Anh
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Hỗ trợ giải đáp thắc mắc, kiểm tra sửa lỗi ngữ pháp và tạo bài trắc nghiệm thông minh từ Google Gemini
        </Typography>
      </Box>

      <ChatBox />
    </Container>
  );
};

export default AiAssistant;
