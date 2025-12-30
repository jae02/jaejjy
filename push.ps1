# ===============================
# Git 프로젝트 업로드 스크립트
# ===============================

$BRANCH = "main"

Write-Host "git check"
git status

$COMMIT_MSG = Read-Host "write commit message"

if ([string]::IsNullOrWhiteSpace($COMMIT_MSG)) {
    Write-Host "fail empty message"
    exit 1
}

git add .
git commit -m "auto commit"
git push origin main

if ($LASTEXITCODE -ne 0) {
    Write-Host "commit fail"
    exit 1
}

git push origin $BRANCH

if ($LASTEXITCODE -eq 0) {
    Write-Host "upload succes"
} else {
    Write-Host "upload fail"
}
