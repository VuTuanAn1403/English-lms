# ==============================================================================
# Script Smoke Test kiểm tra sức khỏe dịch vụ hệ thống English LMS (NFR-09, NFR-10)
# ==============================================================================

param (
    [string]$GatewayUrl = "http://localhost:8080",
    [string]$EurekaUrl = "http://localhost:8761",
    [string]$FrontendUrl = "http://localhost:3000"
)

$ErrorActionPreference = "Continue"

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "      ENGLISH LMS - SMOKE TEST KIỂM TRA HỆ THỐNG   " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Thời gian kiểm thử: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
Write-Host "API Gateway URL:    $GatewayUrl"
Write-Host "Eureka Server URL:  $EurekaUrl"
Write-Host "Frontend URL:       $FrontendUrl"
Write-Host "--------------------------------------------------"

$Endpoints = @(
    @{ Name = "Eureka Discovery Server"; Url = "$EurekaUrl/actuator/health"; ExpectedStatus = 200 },
    @{ Name = "API Gateway Actuator Health"; Url = "$GatewayUrl/actuator/health"; ExpectedStatus = 200 },
    @{ Name = "Public Course Catalog (Course Service)"; Url = "$GatewayUrl/api/v1/courses"; ExpectedStatus = 200 },
    @{ Name = "Public Course Lessons Catalog"; Url = "$GatewayUrl/api/v1/courses/550e8400-e29b-41d4-a716-446655440001/lessons"; ExpectedStatus = 200 }
)

$PassCount = 0

foreach ($ep in $Endpoints) {
    Write-Host "`n[TEST] Kiểm tra $($ep.Name)..." -NoNewline
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $res = Invoke-WebRequest -Uri $ep.Url -Method GET -TimeoutSec 5 -UseBasicParsing
        $sw.Stop()
        $elapsed = $sw.ElapsedMilliseconds

        if ($res.StatusCode -eq $ep.ExpectedStatus) {
            Write-Host " [PASS]" -ForegroundColor Green -NoNewline
            Write-Host " (HTTP $($res.StatusCode), ${elapsed}ms)" -ForegroundColor Gray
            $PassCount++
        } else {
            Write-Host " [FAIL]" -ForegroundColor Red -NoNewline
            Write-Host " (Expected $($ep.ExpectedStatus) but got $($res.StatusCode))" -ForegroundColor Yellow
        }
    } catch {
        $sw.Stop()
        Write-Host " [FAIL]" -ForegroundColor Red -NoNewline
        Write-Host " (Lỗi kết nối: $_)" -ForegroundColor DarkRed
    }
}

# Negative Security Test: Header Spoofing check
Write-Host "`n[SECURITY TEST] Thử giả mạo Header X-User-Role: ADMIN trực tiếp..." -NoNewline
try {
    $secHeaders = @{ "X-User-Role" = "ADMIN"; "X-User-Email" = "fake_admin@test.com" }
    $secRes = Invoke-WebRequest -Uri "$GatewayUrl/api/v1/users" -Headers $secHeaders -Method GET -TimeoutSec 5 -UseBasicParsing
    Write-Host " [VULNERABLE]" -ForegroundColor Red
    Write-Host "  -> CẢNH BÁO: Endpoint bảo vệ trả về HTTP $($secRes.StatusCode) khi gửi header giả mạo!" -ForegroundColor Red
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401 -or $statusCode -eq 403) {
        Write-Host " [PASS]" -ForegroundColor Green -NoNewline
        Write-Host " (Bị từ chối an toàn với HTTP $statusCode)" -ForegroundColor Gray
        $PassCount++
    } else {
        Write-Host " [FAIL]" -ForegroundColor Red -NoNewline
        Write-Host " (Unexpected status code: $statusCode)" -ForegroundColor Yellow
    }
}

Write-Host "`n=================================================="
Write-Host "KẾT QUẢ SMOKE TEST: $PassCount/$($Endpoints.Count + 1) bài test thành công." -ForegroundColor ($PassCount -eq ($Endpoints.Count + 1) ? 'Green' : 'Yellow')
Write-Host "=================================================="
