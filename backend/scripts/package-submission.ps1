# ==============================================================================
# SAFE SUBMISSION PACKAGING SCRIPT (PowerShell for Windows)
# Tự động nén toàn bộ mã nguồn hệ thống English LMS thành file zip nộp bài.
# Loại trừ: .git, target, node_modules, dist, .env, IDE files, log.
# Đảm bảo KHÔNG XÓA hay làm hỏng bất kỳ file nguồn nào.
# ==============================================================================

$projectRoot = Get-Location
$outputZip = Join-Path $projectRoot "english-lms-submission.zip"

Write-Host "📦 Đang khởi tạo quy trình nén bản nộp bài English LMS..." -ForegroundColor Green

if (Test-Path $outputZip) {
    Remove-Item $outputZip -Force
    Write-Host "🗑️ Đã xóa file zip nộp bài cũ: english-lms-submission.zip" -ForegroundColor Yellow
}

$excludePatterns = @(
    "*\.git\*",
    "*\target\*",
    "*\node_modules\*",
    "*\dist\*",
    "*\build\*",
    "*\.env",
    "*\.env.local",
    "*\.idea\*",
    "*\.vscode\*",
    "*\*.log",
    "*\english-lms-submission.zip"
)

Write-Host "⏳ Đang quét và nén các thư mục mã nguồn..." -ForegroundColor Cyan

# Use Compress-Archive with filter
Get-ChildItem -Path $projectRoot -Recurse | Where-Object {
    $itemPath = $_.FullName
    $shouldExclude = $false
    foreach ($pattern in $excludePatterns) {
        if ($itemPath -like $pattern) {
            $shouldExclude = $true
            break
        }
    }
    return -not $shouldExclude
} | Compress-Archive -DestinationPath $outputZip -Update

Write-Host "✅ ĐÃ NÉN BẢN NỘP BÀI THÀNH CÔNG!" -ForegroundColor Green
Write-Host "📁 Đường dẫn file nộp bài: $outputZip" -ForegroundColor Yellow
