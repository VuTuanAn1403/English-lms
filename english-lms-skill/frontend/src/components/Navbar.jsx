import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Avatar,
  Box,
  Menu,
  MenuItem,
  Container,
  Tooltip,
  Chip
} from '@mui/material';
import {
  Menu as MenuIcon,
  School as SchoolIcon,
  SmartToy as AiIcon,
  Person as PersonIcon,
  Logout as LogoutIcon,
  Book as BookIcon,
  Home as HomeIcon,
  Bookmark as BookmarkIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Navbar = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [anchorEl, setAnchorEl] = useState(null);

  const handleOpenMenu = (event) => setAnchorEl(event.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);

  const handleLogout = () => {
    handleCloseMenu();
    logout();
    navigate('/login');
  };

  const navItems = [
    { label: 'Trang chủ', path: '/', icon: <HomeIcon /> },
    { label: 'Khóa học', path: '/courses', icon: <BookIcon /> },
    ...(user ? [
      { label: 'Khóa học của tôi', path: '/my-courses', icon: <BookmarkIcon /> },
      { label: 'Đơn hàng của tôi', path: '/my-orders', icon: <BookIcon /> }
    ] : []),
    { label: 'Trợ lý AI', path: '/ai-assistant', icon: <AiIcon /> },
  ];

  return (
    <AppBar position="sticky" elevation={0} sx={{ background: 'rgba(255,255,255,0.9)', backdropFilter: 'blur(10px)', borderBottom: '1px solid #E2E8F0' }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters sx={{ minHeight: 70 }}>
          <IconButton color="primary" edge="start" onClick={onToggleSidebar} sx={{ mr: 2, display: { md: 'none' } }}>
            <MenuIcon />
          </IconButton>

          <Box sx={{ display: 'flex', alignItems: 'center', cursor: 'pointer', mr: 4 }} onClick={() => navigate('/')}>
            <Box sx={{ p: 1, bgcolor: 'primary.main', borderRadius: 2, display: 'flex', mr: 1.5, color: 'white' }}>
              <SchoolIcon fontSize="medium" />
            </Box>
            <Typography variant="h6" color="text.primary" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
              English<Typography component="span" variant="h6" color="primary" sx={{ fontWeight: 800 }}>LMS</Typography>
            </Typography>
          </Box>

          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, gap: 1 }}>
            {navItems.map((item) => {
              const active = location.pathname === item.path;
              return (
                <Button
                  key={item.path}
                  onClick={() => navigate(item.path)}
                  startIcon={item.icon}
                  sx={{
                    color: active ? 'primary.main' : 'text.secondary',
                    fontWeight: active ? 700 : 600,
                    backgroundColor: active ? 'rgba(79, 70, 229, 0.08)' : 'transparent',
                    borderRadius: 2,
                    px: 2.5,
                    py: 1,
                    '&:hover': { backgroundColor: 'rgba(79, 70, 229, 0.05)', color: 'primary.main' }
                  }}
                >
                  {item.label}
                </Button>
              );
            })}
          </Box>

          <Box sx={{ flexGrow: 0, display: 'flex', alignItems: 'center', gap: 2 }}>
            {user ? (
              <>
                <Chip
                  label={user.role === 'ADMIN' ? 'Quản trị viên' : 'Học viên'}
                  size="small"
                  color={user.role === 'ADMIN' ? 'error' : 'secondary'}
                  sx={{ fontWeight: 700, display: { xs: 'none', sm: 'inline-flex' } }}
                />
                <Tooltip title="Ành đại diện và cài đặt">
                  <IconButton onClick={handleOpenMenu} sx={{ p: 0.5, border: '2px solid #4F46E5' }}>
                    <Avatar alt={user.fullName} src={user.avatar || undefined} sx={{ bgcolor: 'primary.main', fontWeight: 700 }}>
                      {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
                    </Avatar>
                  </IconButton>
                </Tooltip>
                <Menu
                  anchorEl={anchorEl}
                  open={Boolean(anchorEl)}
                  onClose={handleCloseMenu}
                  PaperProps={{
                    elevation: 3,
                    sx: { borderRadius: 3, minWidth: 200, mt: 1.5 }
                  }}
                >
                  <Box sx={{ px: 2, py: 1.5 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{user.fullName}</Typography>
                    <Typography variant="caption" color="text.secondary">{user.email}</Typography>
                  </Box>
                  <MenuItem onClick={() => { handleCloseMenu(); navigate('/my-courses'); }}>
                    <BookmarkIcon fontSize="small" sx={{ mr: 1.5, color: 'text.secondary' }} />
                    Khóa học của tôi
                  </MenuItem>
                  <MenuItem onClick={() => { handleCloseMenu(); navigate('/profile'); }}>
                    <PersonIcon fontSize="small" sx={{ mr: 1.5, color: 'text.secondary' }} />
                    Hồ sơ cá nhân
                  </MenuItem>
                  <MenuItem onClick={() => { handleCloseMenu(); navigate('/ai-assistant'); }}>
                    <AiIcon fontSize="small" sx={{ mr: 1.5, color: 'text.secondary' }} />
                    Trợ lý AI
                  </MenuItem>
                  <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
                    <LogoutIcon fontSize="small" sx={{ mr: 1.5 }} />
                    Đăng xuất
                  </MenuItem>
                </Menu>
              </>
            ) : (
              <Box sx={{ display: 'flex', gap: 1.5 }}>
                <Button variant="outlined" color="primary" onClick={() => navigate('/login')}>
                  Đăng nhập
                </Button>
                <Button variant="contained" color="primary" onClick={() => navigate('/register')}>
                  Đăng ký
                </Button>
              </Box>
            )}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar;
