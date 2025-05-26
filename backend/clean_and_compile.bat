@echo off
echo Maven 프로젝트 클린 및 재컴파일을 진행합니다...

cd /d "C:\SSAFY\JAEWAN\lecture\project\team09-Where2Meow\backend"

echo 1. Maven clean 실행...
call mvn clean

echo 2. Maven compile 실행...
call mvn compile

echo 3. 완료! 서버를 재시작하세요.
pause
