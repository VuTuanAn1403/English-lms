# API GATEWAY - CỔNG TRUY CẬP HỆ THỐNG

## 1. Mô tả
Spring Cloud Gateway - Điểm truy cập trung tâm của hệ thống. Quản lý Routing, Load Balancing (`lb://`), CORS Policy và Lớp xác thực JWT Gateway Filter.

## 2. Thông tin Kỹ thuật
- **Port**: 8080
- **Config Server URL**: `${CONFIG_SERVER_URL:http://config-server:8888}`
- **Discovery Server URL**: `${EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE:http://discovery-server:8761/eureka/}`

## 3. Bảng Routing mặc định
| Path Prefix | Target Microservice |
| :--- | :--- |
| `/api/v1/auth/**`, `/api/v1/users/**` | `user-service` (Port 8081) |
| `/api/v1/courses/**`, `/api/v1/lessons/**`, `/api/v1/enrollments/**`, `/api/v1/orders/**`, `/api/v1/payments/**`, `/api/v1/progress/**`, `/api/v1/admin/**` | `course-service` (Port 8082) |
| `/api/v1/ai/**` | `ai-service` (Port 8083) |

> Tài liệu Kiến trúc đầy đủ xem tại [docs/02-architecture.md](../../docs/02-architecture.md).
