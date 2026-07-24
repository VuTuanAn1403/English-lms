# Frontend - React App

## Mô tả
Giao diện người dùng sử dụng React + Material UI. Toàn bộ giao diện bằng tiếng Việt.

## Công nghệ
- React
- Material UI
- Axios
- React Router

## Cấu trúc (sẽ tạo ở Sprint 5)
```
src/
├── components/
├── pages/
├── layouts/
├── services/
├── hooks/
├── contexts/
├── routes/
├── utils/
├── assets/
└── constants/
```

## Chạy
```bash
npm install
npm run dev
```

## Docker
```bash
docker build -t english-lms/frontend .
docker run -p 3000:3000 english-lms/frontend
```
