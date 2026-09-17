import React from 'react';
import {
  Drawer,
  Box,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Divider,
  Avatar,
  Chip
} from '@mui/material';
import {
  Home as HomeIcon,
  Book as BookIcon,
  SmartToy as AiIcon,
  Person as PersonIcon,
  School as SchoolIcon,
  Logout as LogoutIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Sidebar = ({ open, onClose }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    { label: 'Trang chủ', path: '/', icon: <HomeIcon /> },
    { label: 'Khóa học', path: '/courses', icon: <BookIcon /> },
    { label: 'Trợ lý AI', path: '/ai-assistant', icon: <AiIcon /> },
  ];

  if (user) {
    menuItems.push({ label: 'Hồ sơ cá nhân', path: '/profile', icon: <PersonIcon /> });
  }

  const handleNavigate = (path) => {
    navigate(path);
    onClose();
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    onClose();
  };

  return (
    <Drawer anchor="left" open={open} onClose={onClose}>
      <Box sx={{ width: 280, p: 2, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', p: 1, mb: 2 }}>
          <Box sx={{ p: 1, bgcolor: 'primary.main', borderRadius: 2, display: 'flex', mr: 1.5, color: 'white' }}>
            <SchoolIcon fontSize="medium" />
          </Box>
          <Typography variant="h6" color="text.primary" sx={{ fontWeight: 800 }}>
            English<Typography component="span" variant="h6" color="primary" sx={{ fontWeight: 800 }}>LMS</Typography>
          </Typography>
        </Box>

        <Divider sx={{ mb: 2 }} />

        {user && (
          <Box sx={{ p: 2, bgcolor: 'rgba(79, 70, 229, 0.05)', borderRadius: 3, mb: 2, display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <Avatar alt={user.fullName} src={user.avatar || undefined} sx={{ bgcolor: 'primary.main', fontWeight: 700 }}>
              {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
            </Avatar>
            <Box sx={{ overflow: 'hidden' }}>
              <Typography variant="subtitle2" noWrap sx={{ fontWeight: 700 }}>{user.fullName}</Typography>
              <Chip label={user.role === 'ADMIN' ? 'Quản trị viên' : 'Học viên'} size="small" color={user.role === 'ADMIN' ? 'error' : 'secondary'} sx={{ height: 20, fontSize: '0.7rem' }} />
            </Box>
          </Box>
        )}

        <List sx={{ flexGrow: 1 }}>
          {menuItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <ListItem key={item.path} disablePadding sx={{ mb: 1 }}>
                <ListItemButton
                  onClick={() => handleNavigate(item.path)}
                  selected={active}
                  sx={{
                    borderRadius: 2.5,
                    '&.Mui-selected': {
                      bgcolor: 'primary.main',
                      color: 'white',
                      '& .MuiListItemIcon-root': { color: 'white' },
                      '&:hover': { bgcolor: 'primary.dark' }
                    }
                  }}
                >
                  <ListItemIcon sx={{ minWidth: 40, color: active ? 'white' : 'primary.main' }}>
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: active ? 700 : 600 }} />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>

        <Divider sx={{ my: 2 }} />

        {user ? (
          <ListItem disablePadding>
            <ListItemButton onClick={handleLogout} sx={{ borderRadius: 2.5, color: 'error.main' }}>
              <ListItemIcon sx={{ minWidth: 40, color: 'error.main' }}>
                <LogoutIcon />
              </ListItemIcon>
              <ListItemText primary="Đăng xuất" primaryTypographyProps={{ fontWeight: 600 }} />
            </ListItemButton>
          </ListItem>
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <ListItemButton onClick={() => handleNavigate('/login')} sx={{ borderRadius: 2.5, bgcolor: 'primary.main', color: 'white', justifyContent: 'center' }}>
              <Typography variant="body2" sx={{ fontWeight: 700 }}>Đăng nhập</Typography>
            </ListItemButton>
            <ListItemButton onClick={() => handleNavigate('/register')} sx={{ borderRadius: 2.5, border: '1px solid #4F46E5', color: 'primary.main', justifyContent: 'center' }}>
              <Typography variant="body2" sx={{ fontWeight: 700 }}>Đăng ký</Typography>
            </ListItemButton>
          </Box>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;
