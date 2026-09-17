# English LMS - Pre-Push Checklist

Bảng kiểm tra tính đầy đủ, an toàn và sẵn sàng trước khi commit/push mã nguồn lên GitHub.

- [x] Không có secret (API Key, Private Key, JWT Secret) bị hardcode hoặc track nhầm.
- [x] File `.env` nằm trong `.gitignore` và không bị track trong Git.
- [x] File mẫu `.env.example` đầy đủ tất cả các biến môi trường cần thiết.
- [x] Không có artifact (`target/`, `node_modules/`, `dist/`, `.DS_Store`) bị track trong Git.
- [x] Không có file dung lượng lớn bất thường (>20 MB / >50 MB).
- [x] Chuỗi migration Flyway đầy đủ (V1..V13 trong course-service, V1..V3 trong user-service, V1..V2 trong ai-service).
- [x] Clean database tạo thành công toàn bộ schema từ migration source.
- [x] Backend unit test suite pass (`mvn clean test` - 37/37 tests pass).
- [x] Frontend build pass (`npm run build`).
- [x] Docker build pass (`docker compose build`).
- [x] Cấu hình Docker Compose hợp lệ (`docker compose config --quiet`).
- [x] Tài liệu README.md và PROJECT_REVIEW.md khớp với kiến trúc thực tế.
- [x] GitHub Actions workflow `.github/workflows/ci.yml` hợp lệ.
- [x] Không có accidental deletion (toàn bộ 15 file `D` đã được audit và có lý do chính đáng).
- [x] Cổng thanh toán MOCK chỉ dành cho môi trường demo/dev với thông báo rõ ràng.
- [x] Các hạn chế kỹ thuật thực tế (Known Limitations) được ghi rõ trong tài liệu.
