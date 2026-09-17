# QUY CHUẨN QUẢN LÝ MÃ NGUỒN GITHUB (GITHUB SOURCE CONTROL MANAGEMENT)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Kho mã nguồn chính thức:** `https://github.com/VuTuanAn1403/English-lms`  
**Chủ sở hữu mã nguồn:** `@VuTuanAn1403`  
**Phiên bản quy chuẩn:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. NGUYÊN TẮC QUẢN LÝ NGUỒN SỰ THẬT (SOURCE OF TRUTH PRINCIPLES)

1. **GitHub là nguồn sự thật duy nhất (Single Source of Truth):** Toàn bộ mã nguồn, tài liệu kỹ thuật, kịch bản cơ sở dữ liệu và cấu hình CI/CD phải được đồng bộ chính xác trên GitHub repository.
2. **Không đưa dữ liệu bí mật vào kho mã nguồn (Zero Plaintext Secrets):** Tuyệt đối không commit bất kỳ tệp tin `.env` chứa mật khẩu thật, JWT Secret hoặc Google Gemini API Key. Mọi cấu hình nhạy cảm phải được quản lý qua GitHub Repository Secrets hoặc biến môi trường trên máy chủ/Vercel.
3. **Bảo vệ nhánh chính (`main`):** Nhánh `main` đại diện cho mã nguồn ổn định sẵn sàng triển khai môi trường Production. Cấm thao tác đẩy trực tiếp (`direct push`) hoặc ép ghi đè lịch sử (`force push`). Mọi thay đổi phải thông qua Pull Request có kiểm duyệt.

---

## 2. CHIẾN LƯỢC RẼ NHÁNH (BRANCHING STRATEGY)

Hệ thống áp dụng chiến lược rẽ nhánh có kiểm soát, phù hợp với mô hình phát triển Thác nước (Waterfall):

```
(main) ---------------------------------[Release v1.2]-------------------> (Production)
   \                                          ^
    \--- (feature/* or fix/*) -> [PR + CI] --/
```

| Tên nhánh / Mẫu định danh | Mục đích sử dụng | Quy tắc quản lý |
| :--- | :--- | :--- |
| `main` | Nhánh Production chính thức của hệ thống | Được bảo vệ (Protected); cấm push trực tiếp; chỉ merge khi CI pass và được phê duyệt Stage Gate. |
| `feature/<ten-tinh-nang>` | Phát triển các hạng mục chức năng theo phạm vi Waterfall | Tạo từ `main`; đặt tên rõ nghĩa (ví dụ: `feature/fr06-change-password`); mở PR để merge lại vào `main`. |
| `fix/<ma-loi-hoac-ten>` | Khắc phục các lỗi phát hiện trong quá trình kiểm thử | Liên kết trực tiếp với Defect ID (ví dụ: `fix/def-003-n1-query`). |
| `release/v<version>` | Đóng băng mã nguồn chuẩn bị nghiệm thu phát hành | Tạo khi bước vào giai đoạn kiểm thử chấp nhận cuối cùng; không thêm chức năng mới ngoài bugfix. |
| `hotfix/<su-co-khan-cap>` | Sửa lỗi nghiêm trọng phát sinh trực tiếp trên môi trường vận hành | Tạo từ `main`, sửa lỗi, test tối thiểu và merge trở lại `main` kèm cập nhật phiên bản. |

> [!CAUTION]
> Tuyệt đối không sử dụng các tên nhánh mơ hồ như `temp`, `test123`, `final_v2`, `an_branch`.

---

## 3. QUY CHUẨN ĐẶT TÊN COMMIT (CONVENTIONAL COMMITS)

Mọi commit message phải tuân thủ chuẩn Conventional Commits với cấu trúc:
```
<loai-thay-doi>(<pham-vi>): <mo-ta-ngan-gon-bang-tieng-viet-hoac-anh>
```
Các tiền tố hợp lệ:
- `feat`: Thêm chức năng nghiệp vụ mới (ví dụ: `feat(user): implement FR-06 self-service change password`).
- `fix`: Khắc phục lỗi (ví dụ: `fix(course): resolve N+1 queries in admin enrollments statistics`).
- `refactor`: Tái cấu trúc mã nguồn không làm thay đổi chức năng.
- `docs`: Cập nhật hoặc tạo mới tài liệu kỹ thuật theo Waterfall (ví dụ: `docs: update development plan to linear waterfall`).
- `test`: Thêm hoặc bổ sung ca kiểm thử (ví dụ: `test(ai): add prompt injection guardrail test cases`).
- `chore`: Cấu hình dự án, công cụ build, file `.gitignore`.
- `ci`: Cấu hình workflows GitHub Actions hoặc Vercel deploy.
- `security`: Khắc phục lỗ hổng an toàn, chống header spoofing, rà soát secret hygiene.

---

## 4. QUY TRÌNH PULL REQUEST VÀ PHÊ DUYỆT (PULL REQUEST WORKFLOW)

1. **Tạo nhánh phát triển:** `git checkout -b feature/your-feature-name`.
2. **Kiểm tra cục bộ trước khi push:**
   - Đảm bảo mã nguồn biên dịch thành công (`mvn test` cho backend, `npm run build` cho frontend).
   - Kiểm tra không vô tình add file `.env` hoặc file binary không cần thiết.
3. **Mở Pull Request (PR) vào `main`:**
   - Điền đầy đủ thông tin theo mẫu `.github/PULL_REQUEST_TEMPLATE.md`:
     - Mô tả tóm tắt nội dung thay đổi.
     - Yêu cầu chức năng / Cổng giai đoạn liên quan (FR-xx, Stage Gate x).
     - Kết quả kiểm thử đã thực thi cục bộ.
     - Ảnh chụp màn hình / Bằng chứng kiểm tra.
4. **Tự động hóa kiểm tra (Automated Status Checks):**
   - GitHub Actions kích hoạt các luồng kiểm tra `frontend-ci`, `backend-ci`, `security`.
   - Vercel tạo Preview Deployment để kiểm tra giao diện trực tiếp.
5. **Code Review và Phê duyệt:**
   - Code Owner (`@VuTuanAn1403`) tiến hành rà soát mã nguồn, xác nhận không vi phạm kiến trúc và an toàn thông tin.
   - Nhấn **Squash and Merge** hoặc **Rebase and Merge** để giữ lịch sử commit gọn gàng.
