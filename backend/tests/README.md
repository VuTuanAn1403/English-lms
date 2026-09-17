# TẬP HỢP KIỂM THỬ TẬP TRUNG (ENGLISH LMS CENTRALIZED TEST SUITE)

Hệ thống quản lý kiểm thử tập trung cho toàn bộ hệ thống English LMS, tích hợp tự động hóa từ Unit Test, Security Test, REST API Test đến Integration/Smoke Test trên môi trường Docker.

---

## 📁 1. CẤU TRÚC THƯ MỤC KIỂM THỬ

```text
tests/
├── README.md                  # Hướng dẫn quản lý kiểm thử
├── TEST_INDEX.md              # Bảng chỉ mục ma trận kiểm thử đầy đủ
├── api/                       # API Automated Tests (Python unittest)
│   └── api-test-suite.py
├── smoke/                     # Integration & Smoke Tests (Python script)
│   └── smoke-test.py
├── e2e/                       # E2E Tests (Giao diện & Docker Stack)
│   └── .gitkeep
├── fixtures/                  # Dữ liệu JSON mẫu phục vụ test
│   ├── sample_grammar_request.json
│   └── sample_quiz_request.json
├── legacy/                    # Thư mục lưu trữ các script kiểm thử cũ
│   └── README.md
├── scripts/                   # Script chạy tự động hóa toàn bộ test
│   ├── run-all.ps1            # PowerShell Script (Windows 11)
│   └── run-all.sh             # Bash Script (Linux / macOS / CI)
└── results/                   # Thư mục chứa log kết quả kiểm thử (.gitignore)
    └── .gitkeep
```

> **LƯU Ý VỀ JAVA TEST**: Các Unit Test và Security Test Java của từng Microservice được giữ nguyên tại đường dẫn tiêu chuẩn Maven (`<service>/src/test/java`) nhằm đảm bảo khả năng build và test độc lập từng module. File `TEST_INDEX.md` lưu trữ chỉ mục tới toàn bộ các test này.

---

## 🚀 2. LỆNH CHẠY KIỂM THỬ (QUICK START)

### 2.1. Chạy tất cả Test (Backend Unit + Frontend Build + Docker Compose Check)
- **Windows (PowerShell)**:
  ```powershell
  .\tests\scripts\run-all.ps1
  ```
- **Linux / macOS / Git Bash**:
  ```bash
  chmod +x ./tests/scripts/run-all.sh
  ./tests/scripts/run-all.sh
  ```

### 2.2. Chạy kèm Docker Stack Integration Smoke Test
- **Windows (PowerShell)**:
  ```powershell
  .\tests\scripts\run-all.ps1 -WithDocker
  ```
- **Linux / macOS / Git Bash**:
  ```bash
  ./tests/scripts/run-all.sh --with-docker
  ```

---

## 📋 3. BẢNG CHỈ MỤC KIỂM THỬ (TEST INDEX)

Chi tiết ma trận test cases, lệnh chạy từng file và kết quả xem tại [`TEST_INDEX.md`](./TEST_INDEX.md).
