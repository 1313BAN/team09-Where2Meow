@echo off
echo 기존 캐시된 이미지 파일들을 삭제합니다...
del /Q "src\main\resources\static\images\attractions\*.jpg" 2>nul
echo 캐시 이미지 삭제 완료!
pause
