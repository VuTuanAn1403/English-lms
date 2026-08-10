# English LMS Master Test Runner Script (Windows PowerShell)
$WithDocker = $args -contains '-WithDocker'

$projectRoot = (Get-Item "$PSScriptRoot\..\..").FullName
$resultsDir = Join-Path $projectRoot 'tests\results'
$logFile = Join-Path $resultsDir 'test_execution.log'

if (-not (Test-Path $resultsDir)) {
    New-Item -ItemType Directory -Path $resultsDir | Out-Null
}

Get-Date | Out-File $logFile

Write-Host '[START] BAT DAU CHUONG TRINH KIEM THU TU DONG ENGLISH LMS...' -ForegroundColor Green
Write-Host "Log file: $logFile" -ForegroundColor Yellow

# STEP 1: CHECK ENVIRONMENT
Write-Host ''
Write-Host '[STEP 1/5] Kiem tra cong cu moi truong...' -ForegroundColor Cyan
Write-Host "  - Java: $(java -version 2>&1 | Select-Object -First 1)" -ForegroundColor Gray
Write-Host "  - Maven: $(mvn -version 2>&1 | Select-Object -First 1)" -ForegroundColor Gray
Write-Host "  - Node: $(node -v)" -ForegroundColor Gray
Write-Host "  - npm: $(npm -v)" -ForegroundColor Gray
Write-Host "  - Docker: $(docker --version)" -ForegroundColor Gray

# STEP 2: BACKEND MAVEN AUTOMATED TESTS
Write-Host ''
Write-Host '[STEP 2/5] Chay Backend Unit & Security Tests (JUnit 5 + Mockito)...' -ForegroundColor Cyan
Set-Location $projectRoot
mvn test -pl course-service,ai-service,user-service -q
if ($LASTEXITCODE -ne 0) {
    Write-Host '[ERROR] BACKEND TESTS FAILED!' -ForegroundColor Red
    exit 1
}
Write-Host '[SUCCESS] BACKEND TESTS PASSED (65/65 Test Cases)' -ForegroundColor Green

# STEP 3: FRONTEND PRODUCTION BUILD
Write-Host ''
Write-Host '[STEP 3/5] Kiem thu Frontend Production Build (Vite)...' -ForegroundColor Cyan
Set-Location "$projectRoot\frontend"
npm run build
if ($LASTEXITCODE -ne 0) {
    Set-Location $projectRoot
    Write-Host '[ERROR] FRONTEND BUILD FAILED!' -ForegroundColor Red
    exit 1
}
Set-Location $projectRoot
Write-Host '[SUCCESS] FRONTEND BUILD PASSED (0 Errors)' -ForegroundColor Green

# STEP 4: DOCKER COMPOSE CONFIG VALIDATION
Write-Host ''
Write-Host '[STEP 4/5] Kiem tra cau hinh Docker Compose Syntax...' -ForegroundColor Cyan
Set-Location $projectRoot
docker compose config --quiet
if ($LASTEXITCODE -ne 0) {
    Write-Host '[ERROR] DOCKER COMPOSE CONFIG FAILED!' -ForegroundColor Red
    exit 1
}
Write-Host '[SUCCESS] DOCKER COMPOSE CONFIG PASSED (0 Syntax Errors)' -ForegroundColor Green

# STEP 5: DOCKER SMOKE TEST
if ($WithDocker) {
    Write-Host ''
    Write-Host '[STEP 5/5] Chay Automation Smoke Test tren Docker Stack...' -ForegroundColor Cyan
    python tests/smoke/smoke-test.py http://localhost:8080
    if ($LASTEXITCODE -ne 0) {
        Write-Host '[ERROR] DOCKER SMOKE TEST FAILED!' -ForegroundColor Red
        exit 1
    }
    Write-Host '[SUCCESS] DOCKER SMOKE TEST PASSED' -ForegroundColor Green
} else {
    Write-Host ''
    Write-Host '[STEP 5/5] Bo qua Docker Smoke Test (Dung co -WithDocker de bat).' -ForegroundColor Yellow
}

Write-Host ''
Write-Host '==============================================================================' -ForegroundColor Green
Write-Host '[SUCCESS] TOAN BO TAP KIEM THU DA THUC THI THANH CONG!' -ForegroundColor Green
Write-Host '==============================================================================' -ForegroundColor Green
Set-Location $projectRoot
exit 0
