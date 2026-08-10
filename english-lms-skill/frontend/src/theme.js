import { createTheme } from '@mui/material/styles';

export const getAppTheme = (mode = 'light') =>
  createTheme({
    palette: {
      mode,
      primary: {
        main: '#4F46E5',
        light: '#818CF8',
        dark: '#3730A3',
        contrastText: '#ffffff',
      },
      secondary: {
        main: '#10B981',
        light: '#34D399',
        dark: '#059669',
        contrastText: '#ffffff',
      },
      background: {
        default: mode === 'dark' ? '#0F172A' : '#F8FAFC',
        paper: mode === 'dark' ? '#1E293B' : '#FFFFFF',
      },
      text: {
        primary: mode === 'dark' ? '#F8FAFC' : '#0F172A',
        secondary: mode === 'dark' ? '#94A3B8' : '#475569',
      },
      warning: {
        main: '#F59E0B',
      },
      error: {
        main: '#EF4444',
      },
    },
    typography: {
      fontFamily: '"Plus Jakarta Sans", "Roboto", "Helvetica", "Arial", sans-serif',
      h1: { fontWeight: 800 },
      h2: { fontWeight: 800 },
      h3: { fontWeight: 700 },
      h4: { fontWeight: 700 },
      h5: { fontWeight: 600 },
      h6: { fontWeight: 600 },
      button: { textTransform: 'none', fontWeight: 600 },
    },
    shape: {
      borderRadius: 12,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            borderRadius: 10,
            padding: '8px 20px',
            boxShadow: 'none',
            '&:hover': {
              boxShadow: '0 4px 12px rgba(79, 70, 229, 0.25)',
            },
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 16,
            boxShadow: mode === 'dark' ? '0 4px 20px rgba(0, 0, 0, 0.3)' : '0 4px 20px rgba(0, 0, 0, 0.05)',
            border: mode === 'dark' ? '1px solid #334155' : '1px solid #E2E8F0',
          },
        },
      },
    },
  });

export default getAppTheme('light');
