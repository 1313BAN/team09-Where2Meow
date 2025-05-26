#!/bin/bash
# 기존 캐시된 이미지 파일들을 삭제하는 스크립트

echo "기존 캐시된 이미지 파일들을 삭제합니다..."

CACHE_DIR="src/main/resources/static/images/attractions"
BACKUP_DIR="src/main/resources/static/images/attractions/old_cache"

# 백업 디렉토리가 없으면 생성
if [ ! -d "$BACKUP_DIR" ]; then
    mkdir -p "$BACKUP_DIR"
fi

# 기존 80x80 크기의 이미지들을 백업 디렉토리로 이동
if ls $CACHE_DIR/attraction_*_80x80.jpg 1> /dev/null 2>&1; then
    echo "80x80 크기의 캐시된 이미지들을 백업 디렉토리로 이동..."
    mv $CACHE_DIR/attraction_*_80x80.jpg "$BACKUP_DIR/"
    echo "이동 완료!"
else
    echo "삭제할 80x80 이미지가 없습니다."
fi

# 다른 크기의 캐시된 이미지들도 확인하고 이동
if ls $CACHE_DIR/attraction_*_*x*.jpg 1> /dev/null 2>&1; then
    echo "다른 크기의 캐시된 이미지들을 백업 디렉토리로 이동..."
    mv $CACHE_DIR/attraction_*_*x*.jpg "$BACKUP_DIR/" 2>/dev/null || true
    echo "이동 완료!"
else
    echo "삭제할 기타 크기 이미지가 없습니다."
fi

echo "캐시 이미지 정리 작업이 완료되었습니다."
echo "백업된 이미지들은 $BACKUP_DIR 에서 확인할 수 있습니다."
