# ==============================================================================
# Script phục hồi CSDL PostgreSQL cho hệ thống English LMS (NFR-08)
# ==============================================================================

param (
    [Parameter(Mandatory=$false)]
    [string]$BackupFile,
    [string]$TargetDb = "",
    [string]$DbHost = "localhost",
    [int]$DbPort = 5432,
    [string]$DbUser = "postgres",
    [string]$ContainerName = "english-lms-postgres"
)

$ErrorActionPreference = "Stop"

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  ENGLISH LMS - PHỤC HỒI CƠ SỞ DỮ LIỆU POSTGRESQL " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

if (-not $BackupFile) {
    # Tự động tìm file backup mới nhất trong thư mục .\backups
    $LatestFiles = Get-ChildItem -Path ".\backups" -Filter "*.sql" | Sort-Object LastWriteTime -Descending
    if (-not $LatestFiles) {
        Write-Host "[ERROR] Không tìm thấy file backup nào trong thư mục .\backups!" -ForegroundColor Red
        Write-Host "Vui lòng cung cấp tham số -BackupFile <đường_dẫn>"
        exit 1
    }
    $BackupFile = $LatestFiles[0].FullName
    Write-Host "Tự động chọn file sao lưu mới nhất: $BackupFile" -ForegroundColor Yellow
}

if (-not (Test-Path $BackupFile)) {
    Write-Host "[ERROR] File sao lưu không tồn tại: $BackupFile" -ForegroundColor Red
    exit 1
}

# Đoán TargetDb từ tên file nếu chưa truyền
if (-not $TargetDb) {
    $FileName = Split-Path $BackupFile -Leaf
    if ($FileName -match "^(user_db|course_db|ai_db)") {
        $TargetDb = $matches[1]
    } else {
        Write-Host "[ERROR] Không xác định được CSDL đích. Vui lòng chỉ định -TargetDb (user_db, course_db, ai_db)" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Phục hồi vào cơ sở dữ liệu: [$TargetDb]" -ForegroundColor Cyan
Write-Host "Nguồn: $BackupFile"

try {
    $dockerRunning = (docker ps -q -f name=$ContainerName 2>$null)

    if ($dockerRunning) {
        Write-Host "  -> Phục hồi qua Docker container [$ContainerName]..." -ForegroundColor Gray
        Get-Content $BackupFile | docker exec -i $ContainerName psql -U $DbUser -d $TargetDb
    } else {
        Write-Host "  -> Phục hồi trực tiếp tới host..." -ForegroundColor Gray
        psql -h $DbHost -p $DbPort -U $DbUser -d $TargetDb -f $BackupFile
    }

    Write-Host "[PASS] Phục hồi CSDL [$TargetDb] thành công!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Quá trình phục hồi gặp sự cố: $_" -ForegroundColor Red
    exit 1
}
