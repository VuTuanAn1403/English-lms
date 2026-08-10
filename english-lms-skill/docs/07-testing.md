# TÀI LIỆU CHIẾN LƯỢC VÀ KẾT QUẢ KIỂM THỬ (07-TESTING)

---

## 1. THÁP KIỂM THỬ HỆ THỐNG (TEST PYRAMID)

```text
               / \
              /   \      Automation Smoke Test Script (Python: scripts/smoke-test.py)
             /     \     Bao phủ 12 bước end-to-end trên Docker Stack
            /-------\
           /         \   Integration Tests (MockMvc & Service Integration)
          /           \  Kiểm thử luồng Đăng ký, Học thử, Thanh toán, Doanh thu
         /-------------\
        /               \ Unit & Security Tests (JUnit 5 + Mockito)
       /                 \ 65 Test Cases bao phủ JWT, RBAC, Data Validation
      /-------------------\
```

---

## 2. KẾT QUẢ KIỂM THỬ TỰ ĐỘNG THỰC TẾ

*(Trích xuất từ kết quả chạy thực tế ngày 10/08/2026)*

| Microservice Module | Lớp Kiểm Thử (Test Class) | Số Test Cases | Kết Quả |
| :--- | :--- | :---: | :---: |
| `user-service` | `UserServiceTest`, `UserSecurityTest`, `ApplicationTests` | **7** | ✅ 7/7 PASS |
| `course-service` | `CourseServiceTest`, `CourseSecurityTest`, `OrderServiceTest`, `ApplicationTests` | **19** | ✅ 19/19 PASS |
| `ai-service` | `AiServiceTest`, `AiSecurityTest`, `PromptEngineTest`, `ChatMemoryTest`, `GeminiIntegrationTest`, `AiExceptionTest`, `AiControllerTest` | **37** | ✅ 37/37 PASS |
| `api-gateway` | `JwtUtilTest` | **2** | ✅ 2/2 PASS |
| **TỔNG CỘNG BACKEND** | | **65** | **✅ 65/65 PASS (100%)** |

- **Frontend Production Build (`npm run build`)**: 11,635 modules transformed -> **0 ERRORS**.
- **Docker Compose Spec (`docker compose config --quiet`)**: **0 ERRORS**.

---

## 3. PHÂN BIỆT THỦ CÔNG VÀ TỰ ĐỘNG

- **Automated Tests**: Chạy hoàn toàn độc lập thông qua Maven (`mvn test`) và Python Smoke Script (`python scripts/smoke-test.py`). Toàn bộ luồng kết nối AI ngoài được Mocking để đảm bảo tốc độ và độ tin cậy.
- **Manual Verification**: Kiểm thử giao diện người dùng (UI Flow), nhúng Iframe YouTube, tải file PDF tài liệu và trải nghiệm thực tế với Google Gemini API Key thật.
