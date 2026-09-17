import React from 'react';
import { Box } from '@mui/material';
import ChatBox from '../components/ChatBox';

const AiAssistant = () => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        overflow: 'hidden',
        bgcolor: '#f8fafc'
      }}
      className="animate-fade-in"
    >
      <ChatBox />
    </Box>
  );
};

export default AiAssistant;
