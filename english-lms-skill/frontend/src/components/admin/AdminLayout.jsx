import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  List,
  Typography,
  Divider,
  IconButton,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Avatar,
  Menu,
  MenuItem,
  Breadcrumbs,
  Tooltip,
  Badge,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import {
  Menu as MenuIcon,
  ChevronLeft as ChevronLeftIcon,
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  MenuBook as CoursesIcon,
  AutoStories as LessonsIcon,
  AssignmentTurnedIn as EnrollmentIcon,
  AttachMoney as RevenueIcon,
  Psychology as AiIcon,
  Person as ProfileIcon,
  Settings as SettingsIcon,
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
  Notifications as NotificationsIcon,
  Logout as LogoutIcon,
  School as SchoolIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useColorMode } from '../../contexts/ThemeContext';

const DRAWER_WIDTH = 260;

const navItems = [
  { text: 'Dashboard', path: '/admin', icon: <DashboardIcon /> },
  { text: 'Doanh thu', path: '/admin/revenue', icon: <RevenueIcon /> },
  { text: 'Users', path: '/admin/users', icon: <PeopleIcon /> },
  { text: 'Courses', path: '/admin/courses', icon: <CoursesIcon /> },
  { text: 'Lessons', path: '/admin/lessons', icon: <LessonsIcon /> },
  { text: 'Enrollments', path: '/admin/enrollments', icon: <EnrollmentIcon /> },
  { text: 'AI Management', path: '/admin/ai', icon: <AiIcon /> },
  { text: 'Profile', path: '/admin/profile', icon: <ProfileIcon /> },
  { text: 'Settings', path: '/admin/settings', icon: <SettingsIcon /> },
];

const AdminLayout = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [desktopOpen, setDesktopOpen] = useState(true);
  const [anchorEl, setAnchorEl] = useState(null);

  const { user, logout } = useAuth();
  const { mode, toggleColorMode } = useColorMode();
  const navigate = useNavigate();
  const location = useLocation();

  const handleDrawerToggle = () => {
    if (isMobile) {
      setMobileOpen(!mobileOpen);
    } else {
      setDesktopOpen(!desktopOpen);
    }
  };

  const handleMenuOpen = (event) => setAnchorEl(event.currentTarget);
  const handleMenuClose = () => setAnchorEl(null);

  const handleLogout = () => {
    handleMenuClose();
    logout();
    navigate('/admin/login');
  };

  const pathNames = location.pathname.split('/').filter((x) => x);

  const drawerContent = (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Toolbar
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          px: [2],
          bgcolor: theme.palette.mode === 'dark' ? '#0F172A' : '#4F46E5',
          color: '#ffffff',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <SchoolIcon sx={{ fontSize: 28 }} />
          <Typography variant="h6" fontWeight="bold" sx={{ letterSpacing: 0.5 }}>
            LMS Admin
          </Typography>
        </Box>
        <IconButton onClick={handleDrawerToggle} sx={{ color: '#ffffff' }}>
          <ChevronLeftIcon />
        </IconButton>
      </Toolbar>
      <Divider />
      <List sx={{ flexGrow: 1, px: 1.5, py: 2 }}>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path || (item.path !== '/admin' && location.pathname.startsWith(item.path));
          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                component={Link}
                to={item.path}
                onClick={() => isMobile && setMobileOpen(false)}
                sx={{
                  borderRadius: 2,
                  bgcolor: isActive
                    ? theme.palette.mode === 'dark'
                      ? 'rgba(79, 70, 229, 0.25)'
                      : 'rgba(79, 70, 229, 0.1)'
                    : 'transparent',
                  color: isActive ? 'primary.main' : 'text.primary',
                  '&:hover': {
                    bgcolor: theme.palette.mode === 'dark' ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.04)',
                  },
                }}
              >
                <ListItemIcon sx={{ color: isActive ? 'primary.main' : 'text.secondary', minWidth: 40 }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontSize: 14,
                    fontWeight: isActive ? 700 : 500,
                  }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Divider />
      <Box sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Avatar src={user?.avatar} alt={user?.fullName} sx={{ bgcolor: 'primary.main' }}>
          {user?.fullName?.charAt(0) || 'A'}
        </Avatar>
        <Box sx={{ overflow: 'hidden' }}>
          <Typography variant="subtitle2" noWrap fontWeight="bold">
            {user?.fullName || 'Admin User'}
          </Typography>
          <Typography variant="caption" color="text.secondary" noWrap display="block">
            {user?.email || 'admin@gmail.com'}
          </Typography>
        </Box>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* Top AppBar */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { md: desktopOpen ? `calc(100% - ${DRAWER_WIDTH}px)` : '100%' },
          ml: { md: desktopOpen ? `${DRAWER_WIDTH}px` : 0 },
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
          }),
          bgcolor: 'background.paper',
          color: 'text.primary',
          borderBottom: 1,
          borderColor: 'divider',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>

          {/* Breadcrumbs */}
          <Box sx={{ flexGrow: 1 }}>
            <Breadcrumbs aria-label="breadcrumb" sx={{ color: 'text.secondary', fontSize: 14 }}>
              <Link to="/admin" style={{ textDecoration: 'none', color: 'inherit' }}>
                Admin
              </Link>
              {pathNames.slice(1).map((name, index) => {
                const routeTo = `/${pathNames.slice(0, index + 2).join('/')}`;
                const isLast = index === pathNames.slice(1).length - 1;
                return isLast ? (
                  <Typography key={name} color="text.primary" fontSize={14} fontWeight={600} sx={{ textTransform: 'capitalize' }}>
                    {name}
                  </Typography>
                ) : (
                  <Link key={name} to={routeTo} style={{ textDecoration: 'none', color: 'inherit', textTransform: 'capitalize' }}>
                    {name}
                  </Link>
                );
              })}
            </Breadcrumbs>
          </Box>

          {/* Actions */}
          <Tooltip title="Chuyển đổi giao diện">
            <IconButton onClick={toggleColorMode} color="inherit" sx={{ mr: 1 }}>
              {mode === 'dark' ? <LightModeIcon sx={{ color: '#F59E0B' }} /> : <DarkModeIcon />}
            </IconButton>
          </Tooltip>

          <Tooltip title="Thông báo">
            <IconButton color="inherit" sx={{ mr: 1 }}>
              <Badge badgeContent={3} color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
          </Tooltip>

          {/* User Menu */}
          <IconButton onClick={handleMenuOpen} sx={{ p: 0.5 }}>
            <Avatar src={user?.avatar} alt={user?.fullName} sx={{ width: 36, height: 36, bgcolor: 'primary.main' }}>
              {user?.fullName?.charAt(0) || 'A'}
            </Avatar>
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            onClick={handleMenuClose}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          >
            <MenuItem component={Link} to="/admin/profile">
              <ListItemIcon><ProfileIcon fontSize="small" /></ListItemIcon>
              Hồ sơ cá nhân
            </MenuItem>
            <MenuItem component={Link} to="/admin/settings">
              <ListItemIcon><SettingsIcon fontSize="small" /></ListItemIcon>
              Cài đặt
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
              <ListItemIcon><LogoutIcon fontSize="small" color="error" /></ListItemIcon>
              Đăng xuất
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Drawer */}
      <Box
        component="nav"
        sx={{ width: { md: desktopOpen ? DRAWER_WIDTH : 0 }, flexShrink: { md: 0 } }}
      >
        {isMobile ? (
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onClose={handleDrawerToggle}
            ModalProps={{ keepMounted: true }}
            sx={{
              '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
            }}
          >
            {drawerContent}
          </Drawer>
        ) : (
          <Drawer
            variant="persistent"
            open={desktopOpen}
            sx={{
              '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
            }}
          >
            {drawerContent}
          </Drawer>
        )}
      </Box>

      {/* Main Content Area */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: desktopOpen ? `calc(100% - ${DRAWER_WIDTH}px)` : '100%' },
          mt: 8,
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
          }),
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
};

export default AdminLayout;
