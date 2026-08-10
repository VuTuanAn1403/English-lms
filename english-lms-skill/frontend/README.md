# FRONTEND - WEB APPLICATION (REACT + VITE)

## 1. Mô tả
Ứng dụng Web Client dành cho Học viên và Quản trị viên. Giao diện thiết kế theo ngôn ngữ Material UI (MUI) tiếng Việt, tích hợp AI Assistant Chat, Học thử bài học, Thanh toán đơn hàng và Báo cáo Doanh thu Admin.

## 2. Công nghệ & Thư viện
- **React**: 18.3.1
- **Vite**: 6.4.3
- **Material UI**: 6.1.10
- **Axios**: 1.7.9 (Interceptor xác thực Token JWT & 401 Redirect)
- **React Router DOM**: 6.28.0

## 3. Lệnh khởi chạy và Đóng gói
```bash
# Cài đặt thư viện
npm ci

# Chạy môi trường phát triển local (Port 5173)
npm run dev

# Đóng gói sản phẩm Production (dist/)
npm run build
```

> Hướng dẫn triển khai Docker Compose xem tại [README.md](../README.md).
