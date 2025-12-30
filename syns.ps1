# ===============================
# Git 프로젝트 다운로드/동기화
# ===============================

$REPO_URL = "git@github.com:jae02/jaejjy.git"
$PROJECT_DIR = "jaejjy"
$BRANCH = "main"

if (Test-Path $PROJECT_DIR) {
    Write-Host "load program"
    Set-Location $PROJECT_DIR

    git checkout $BRANCH
    git pull origin $BRANCH

    if ($LASTEXITCODE -eq 0) {
        Write-Host "success"
    } else {
        Write-Host "fail"
    }
} else {
    Write-Host "new download"
    git clone -b $BRANCH $REPO_URL

    if ($LASTEXITCODE -eq 0) {
        Write-Host "download success"
    } else {
        Write-Host "download fail"
    }
}
