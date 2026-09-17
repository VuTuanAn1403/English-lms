# ==============================================================================
# Script sao lưu CSDL PostgreSQL cho hệ thống English LMS (NFR-08)
# ==============================================================================

param (
    [string]$BackupDir = ".\backups",
    [string]$DbHost = "localhost",
    [int]$DbPort = 5432,
    [string]$DbUser = "postgres",
    [string]$ContainerName = "english-lms-postgres"
)

$ErrorActionPreference = "Stop"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  ENGLISH LMS - SAO LƯU CƠ SỞ DỮ LIỆU POSTGRESQL  " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Thời gian: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
Write-Host "Thư mục đích: $BackupDir"

if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
    Write-Host "[OK] Đã tạo thư mục sao lưu: $BackupDir" -ForegroundColor Green
}

$Databases = @("user_db", "course_db", "ai_db")
$SuccessCount = 0

foreach ($Db in $Databases) {
    $OutputFile = Join-Path $BackupDir "${Db}_backup_${Timestamp}.sql"
    Write-Host "`nĐang sao lưu cơ sở dữ liệu [$Db]..." -ForegroundColor Yellow

    try {
        # Kiểm tra nếu container Docker postgres đang chạy
        $dockerRunning = (docker ps -q -f name=$ContainerName 2>$null)

        if ($dockerRunning) {
            Write-Host "  -> Thực hiện pg_dump qua Docker container [$ContainerName]..." -ForegroundColor Gray
            docker exec -t $ContainerName pg_dump -U $DbUser -d $Db --clean --if-exists > $OutputFile
        } else {
            Write-Host "  -> Thực hiện pg_dump trực tiếp từ host..." -ForegroundColor Gray
            pg_dump -h $DbHost -p $DbPort -U $DbUser -d $Db --clean --if-exists -f $OutputFile
        }

        if ((Test-Path $OutputFile) -and ((Get-Item $OutputFile).Length -gt 0)) {
            $SizeKB = [math]::Round((Get-Item $OutputFile).Length / 1024, 2)
            Write-Host "[PASS] Sao lưu thành công: $OutputFile ($SizeKB KB)" -ForegroundColor Green
            $SuccessCount++
        } else {
            Write-Host "[FAIL] File sao lưu trống hoặc không tồn tại: $OutputFile" -ForegroundColor Red
        }
    } catch {
        Write-Host "[ERROR] Lỗi khi sao lưu [$Db]: $_" -ForegroundColor Red
    }
}

Write-Host "`n--------------------------------------------------"
Write-Host "Tổng kết: $SuccessCount/$($Databases.Count) cơ sở dữ liệu đã được sao lưu thành công." -ForegroundColor ($SuccessCount -eq $Databases.Count ? 'Green' : 'Yellow')
Write-Host "=================================================="
