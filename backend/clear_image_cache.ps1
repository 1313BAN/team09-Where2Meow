# PowerShell 스크립트: 기존 80x80 이미지들을 old_cache 폴더로 이동

$sourceDir = "src\main\resources\static\images\attractions"
$targetDir = "src\main\resources\static\images\attractions\old_cache"

# old_cache 디렉토리가 없으면 생성
if (!(Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir -Force
    Write-Host "old_cache 디렉토리 생성됨: $targetDir"
}

# 80x80 패턴의 이미지 파일들을 찾아서 이동
$files = Get-ChildItem -Path $sourceDir -Name "*_80x80.jpg"

Write-Host "이동할 파일 수: $($files.Count)"

foreach ($file in $files) {
    $sourcePath = Join-Path $sourceDir $file
    $targetPath = Join-Path $targetDir $file
    
    try {
        Move-Item -Path $sourcePath -Destination $targetPath -Force
        Write-Host "이동 완료: $file"
    } catch {
        Write-Error "이동 실패: $file - $_"
    }
}

# 다른 크기 패턴의 이미지들도 확인하고 이동
$otherFiles = Get-ChildItem -Path $sourceDir -Name "*_*x*.jpg" | Where-Object { $_ -notlike "*_80x80.jpg" }

if ($otherFiles.Count -gt 0) {
    Write-Host "다른 크기 이미지 파일 수: $($otherFiles.Count)"
    
    foreach ($file in $otherFiles) {
        $sourcePath = Join-Path $sourceDir $file
        $targetPath = Join-Path $targetDir $file
        
        try {
            Move-Item -Path $sourcePath -Destination $targetPath -Force
            Write-Host "이동 완료: $file"
        } catch {
            Write-Error "이동 실패: $file - $_"
        }
    }
}

Write-Host "모든 캐시 이미지 정리 완료!"
Write-Host "정리된 이미지들은 $targetDir 에서 확인할 수 있습니다."
