#!/bin/bash
# ==============================================================================
# SAFE SUBMISSION PACKAGING SCRIPT (Bash for Linux/macOS)
# Tự động nén toàn bộ mã nguồn hệ thống English LMS thành file zip nộp bài.
# Loại trừ: .git, target, node_modules, dist, .env, IDE files, log.
# Đảm bảo KHÔNG XÓA hay làm hỏng bất kỳ file nguồn nào.
# ==============================================================================

OUTPUT_ZIP="english-lms-submission.zip"

echo "📦 Đang khởi tạo quy trình nén bản nộp bài English LMS..."

if [ -f "$OUTPUT_ZIP" ]; then
    rm -f "$OUTPUT_ZIP"
    echo "🗑️ Đã xóa file zip nộp bài cũ."
fi

zip -r "$OUTPUT_ZIP" . \
  -x "*.git*" \
  -x "*target/*" \
  -x "*node_modules/*" \
  -x "*dist/*" \
  -x "*.env*" \
  -x "*.idea*" \
  -x "*.vscode*" \
  -x "*.log" \
  -x "$OUTPUT_ZIP"

echo "✅ ĐÃ NÉN BẢN NỘP BÀI THÀNH CÔNG: $OUTPUT_ZIP"
