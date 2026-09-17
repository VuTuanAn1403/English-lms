import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';

const Loading = ({ message = 'Đang tải dữ liệu...' }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '300px',
        width: '100%',
        gap: 2,
      }}
    >
      <CircularProgress size={48} color="primary" />
      <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 600 }}>
        {message}
      </Typography>
    </Box>
  );
};

export default Loading;
