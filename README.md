# 🐱 Where2Meow (어디가냥)

**여행 계획 및 관광지 추천 플랫폼**

여행을 계획하고 다른 사용자들과 여행 경험을 공유할 수 있는 종합 여행 플랫폼입니다.

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [설치 및 실행](#-설치-및-실행)
- [API 문서](#-api-문서)
- [데이터베이스 구조](#-데이터베이스-구조)
- [프로젝트 구조](#-프로젝트-구조)
- [개발팀](#-개발팀)

## 🎯 프로젝트 개요

Where2Meow는 사용자가 여행 계획을 세우고, 관광지 정보를 검색하며, 다른 사용자들과 여행 경험을 공유할 수 있는 통합 여행 플랫폼입니다. AI 기반 추천 시스템을 통해 개인화된 여행 계획을 제공합니다.

## ✨ 주요 기능

### 🗺️ 여행 계획
- **개인화된 여행 계획 생성**: 일정별 관광지 배치 및 경로 최적화
- **드래그 앤 드롭 인터페이스**: 직관적인 일정 관리
- **지역별 관광지 검색**: 실시간 검색 및 필터링
- **계획 공유 및 북마크**: 다른 사용자의 계획 참고

### 🏛️ 관광지 정보
- **상세 관광지 정보**: 위치, 연락처, 운영시간 등
- **고화질 이미지 캐싱**: 빠른 로딩을 위한 이미지 최적화
- **사용자 리뷰 및 평점**: 실제 방문 후기
- **지도 연동**: Google Maps를 통한 위치 확인

### 👥 커뮤니티
- **게시판 시스템**: 여행 후기, 질문, 정보 공유
- **좋아요 및 북마크**: 유용한 게시글 저장
- **댓글 시스템**: 활발한 소통

### 🤖 AI 여행 계획 생성
- **RAG 기반 여행 계획 추천**: FAISS 벡터 데이터베이스를 활용한 유사 여행 계획 검색
- **GPT-4 기반 맞춤형 일정 생성**: 사용자 요청에 따른 개인화된 여행 계획 자동 생성
- **지능형 일정 수정**: 기존 계획에 관광지 추가/삭제 및 최적화
- **자동 설명 생성**: 생성된 여행 계획에 대한 친근한 설명 제공
- **실시간 관광지 정보 연동**: 데이터베이스와 연동하여 실제 존재하는 관광지만 추천

## 🛠️ 기술 스택

### Frontend
- **Vue.js 3**: 모던 프론트엔드 프레임워크
- **Vite**: 빠른 빌드 도구
- **Pinia**: 상태 관리
- **Vue Router**: 라우팅
- **TailwindCSS**: 스타일링
- **PrimeVue**: UI 컴포넌트 라이브러리
- **Google Maps API**: 지도 서비스
- **Axios**: HTTP 클라이언트

### Backend
- **Spring Boot 3.4.5**: Java 기반 백엔드 프레임워크
- **Spring Security**: 보안 및 인증
- **Spring Data JPA**: 데이터 접근 계층
- **MySQL**: 관계형 데이터베이스
- **JWT**: 토큰 기반 인증
- **Argon2**: 비밀번호 암호화
- **Swagger**: API 문서화

### AI Service
- **FastAPI**: Python 기반 고성능 API 프레임워크
- **LangChain**: LLM 애플리케이션 개발 프레임워크
- **OpenAI GPT-4**: 대화형 AI 모델
- **FAISS**: 고속 벡터 유사도 검색 엔진
- **RAG (Retrieval-Augmented Generation)**: 검색 증강 생성 시스템

### DevOps & Tools
- **Maven**: 빌드 도구
- **Git**: 버전 관리
- **Docker**: 컨테이너화 (준비중)

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue.js App    │────│  Spring Boot    │────│     MySQL       │
│   (Frontend)    │    │   (Backend)     │    │   (Database)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        
                       ┌─────────────────┐               
                       │   FastAPI       │
                       │  (AI Service)   │
                       └─────────────────┘
                                │
                       ┌─────────────────┐
                       │  Vector Store   │
                       │  (FAISS/Chroma) │
                       └─────────────────┘
                                │
                       ┌─────────────────┐
                       │   OpenAI API    │
                       │   (GPT-4)       │
                       └─────────────────┘
```

## 🚀 실행

### 사전 요구사항
- Java 17 이상
- Node.js 18 이상
- MySQL 8.0 이상
- Python 3.12


### 1. 백엔드 실행
```bash
cd backend
./mvnw spring-boot:run
```

### 2. 프론트엔드 설정 및 실행
```bash
cd frontend
npm install
npm run dev
```

### 3. AI 서비스 설정 및 실행
```bash
cd AI

# 가상환경 생성 (권장)
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install -r requirements.txt

# 환경변수 설정 (.env 파일 수정)
# OpenAI API 키 설정 필수
# 데이터베이스 연결 정보 확인

# AI 서비스 실행
cd app
uvicorn main:app --reload --port 8000
```

## 📚 API 문서

백엔드 서버 실행 후 다음 주소에서 API 문서를 확인할 수 있습니다:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 주요 API 엔드포인트

#### 관광지
- `GET /api/attraction` - 관광지 목록 조회
- `GET /api/attraction/detail/{attractionId}` - 관광지 상세 정보
- `GET /api/attraction/search` - 관광지 검색

#### 사용자 인증
- `POST /api/user/login` - 로그인
- `POST /api/user/register` - 회원가입
- `POST /api/user/logout` - 로그아웃

#### 여행 계획
- `GET /api/plan` - 여행 계획 목록
- `POST /api/plan` - 여행 계획 생성
- `PUT /api/plan/{planId}` - 여행 계획 수정
- `DELETE /api/plan/{planId}` - 여행 계획 삭제

#### AI 여행 계획 생성
- `POST /ai/create` - AI 기반 여행 계획 생성
- `POST /ai/update` - 기존 계획 AI 기반 수정

#### 게시판
- `GET /api/board` - 게시글 목록
- `POST /api/board` - 게시글 작성
- `PUT /api/board/{boardId}` - 게시글 수정
- `DELETE /api/board/{boardId}` - 게시글 삭제

## 🗄️ 데이터베이스 구조

### 주요 테이블

#### 사용자 관련
- **user**: 사용자 정보
- **user_role**: 사용자 권한

#### 관광지 관련
- **attraction**: 관광지 정보
- **attraction_category**: 관광지 카테고리
- **review**: 관광지 리뷰
- **review_like**: 리뷰 좋아요

#### 지역 정보
- **country**: 국가 정보
- **state**: 시/도 정보
- **city**: 구/군 정보

#### 여행 계획
- **plan**: 여행 계획
- **plan_attraction**: 계획별 관광지
- **plan_like**: 계획 좋아요
- **plan_bookmark**: 계획 북마크

#### 커뮤니티
- **board**: 게시글
- **board_category**: 게시글 카테고리
- **comment**: 댓글
- **board_like**: 게시글 좋아요
- **board_bookmark**: 게시글 북마크

## 📁 프로젝트 구조

```
team09-Where2Meow/
├── backend/                          # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ssafy/where2meow/
│   │   │   │   ├── attraction/      # 관광지 관련
│   │   │   │   ├── user/           # 사용자 관련
│   │   │   │   ├── plan/           # 여행계획 관련
│   │   │   │   ├── board/          # 게시판 관련
│   │   │   │   ├── review/         # 리뷰 관련
│   │   │   │   ├── common/         # 공통 서비스
│   │   │   │   ├── config/         # 설정
│   │   │   │   └── exception/      # 예외 처리
│   │   │   └── resources/
│   │   │       ├── static/images/  # 이미지 캐시
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/                         # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/             # 공통 컴포넌트
│   │   │   └── views/              # 뷰별 컴포넌트
│   │   ├── views/                  # 페이지 뷰
│   │   ├── stores/                 # Pinia 스토어
│   │   ├── api/                    # API 호출
│   │   ├── utils/                  # 유틸리티
│   │   └── router/                 # 라우터 설정
│   ├── public/
│   └── package.json
├── AI/                               # FastAPI AI 서비스
│   ├── app/
│   │   ├── main.py                 # FastAPI 메인 서버
│   │   ├── config.py               # 설정 파일
│   │   ├── rag/                    # RAG 시스템
│   │   │   ├── plan_search.py        # 여행계획 RAG 검색
│   │   │   └── attraction_rag.py     # 관광지 RAG 검색
│   │   ├── generator/              # AI 생성 모듈
│   │   │   ├── create_rag.py         # 여행계획 생성
│   │   │   └── description.py        # 설명 생성
│   │   ├── util/                   # 유틸리티
│   │   │   └── merge_plan.py         # 계획 병합
│   │   ├── database/               # 벡터 DB
│   │   ├── data/                   # 백터 DB 생성 모듈
│   │   ├── validation/             # 입력 검증
│   └── requirements.txt            # Python 의존성
└── README.md
```

## 🔧 개발 설정

### 환경 변수 설정

#### Frontend (.env.local)
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_GOOGLE_MAPS_API_KEY=your_google_maps_api_key
```

#### Backend (application.properties)
```properties
# 데이터베이스 설정
spring.datasource.url=your_database_url
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT 설정
jwt.secret=your_jwt_secret_key
jwt.token-validity-in-seconds=86400

# 서버 설정
app.base-url=http://localhost:8080

# AI 서비스 연동
ai.service.base-url=http://localhost:8000
```

#### AI Service (.env)
```env
# OpenAI API 설정 (필수)
OPENAI_API_KEY=your_openai_api_key

# 데이터베이스 연결
DB_HOST=your_database_url
DB_PORT=3306
DB_NAME=your_database_name
DB_USER=your_username
DB_PASS=your_password
```

### 개발 도구 설정

#### 코드 스타일
- **Frontend**: ESLint + Prettier
- **Backend**: Google Java Style Guide

#### IDE 권장사항
- **Frontend**: VS Code + Vetur/Volar
- **Backend**: IntelliJ IDEA + Lombok Plugin


## 🔒 보안

- **JWT 토큰**: 사용자 인증
- **Argon2**: 비밀번호 해싱
- **CORS**: 크로스 오리진 설정
- **Input Validation**: 입력 데이터 검증
- **SQL Injection 방지**: JPA 사용

## 📈 성능 최적화

### 백엔드 최적화
- **이미지 캐싱**: 하이브리드 이미지 서비스
- **데이터베이스 인덱싱**: 검색 성능 향상
- **Connection Pooling**: HikariCP 사용
- **정적 리소스 캐싱**: 1년 캐시 설정

### AI 서비스 최적화
- **FAISS 벡터 검색**: 고속 유사도 검색 (30,000건 1초 이내)
- **비동기 처리**: FastAPI 비동기 엔드포인트
- **캐싱 전략**: 벡터 임베딩 로컬 캐싱
- **배치 처리**: 다중 관광지 정보 일괄 검색
- **재시도 로직**: 실패 시 자동 재시도로 안정성 확보

### 프론트엔드 최적화
- **Vite 빌드**: 빠른 개발 서버 및 HMR
- **컴포넌트 지연 로딩**: 성능 향상
- **API 응답 캐싱**: 중복 요청 방지

## 🤖 AI 서비스 상세 기능

### RAG (Retrieval-Augmented Generation) 시스템

#### 여행 계획 RAG 검색
- **FAISS 벡터 데이터베이스**: 기존 인기 여행 계획 50,000여 건 색인
- **지능형 쿼리 파싱**: GPT-4로 사용자 질문에서 지역, 기간, 여행스타일 추출
- **다단계 필터링**: 위치, 기간, 계절 기반 맞춤형 검색
- **인기도 기반 랜덤 선택**: 상위 인기 계획 중 가중치 랜덤 선택

#### 관광지 RAG 검색
- **실시간 데이터베이스 연동**: MySQL에서 최신 관광지 정보 검색
- **카테고리 기반 분류**: 관광지(category_id != 39), 식당(category_id = 39) 구분
- **지역 제한 검색**: 사용자 요청 지역 내 또는 차로 30분 이내 지역만 추천

### AI 여행 계획 생성 알고리즘

#### 생성 프로세스
1. **RAG 검색**: 유사 여행 계획 검색 및 참고
2. **GPT-4 기반 생성**: 개인화된 일정 생성
3. **지역 제한 검증**: 사용자 요청 지역 내 관광지만 선택
4. **이동 거리 최적화**: 차로 30분 이상 이동 경로 배제
5. **일정 밸런싱**: 아침-점심-오후-저녁-야간 세밀한 시간 계획

#### 일정 구성 패턴
- **아침**: 관광지 1곳 (2시간)
- **점심**: 식당 1곳 (1시간)
- **오후**: 관광지 2곳 (4시간)
- **저녁**: 식당 1곳 (1시간)
- **야간**: 관광지 1곳 (2시간)

### 지능형 일정 수정 시스템

#### 수정 작업 처리
- **추가 요청**: 기존 일정 분석 후 적절한 위치에 삽입
- **삭제 요청**: uniqueKey 기반 정확한 삭제
- **순서 재정렬**: 날짜별 attractionOrder 자동 재배치
- **중복 방지**: 관광지와 식당 중복 배제 로직

#### 삽입 위치 알고리즘
- **앞쪽 삽입**: 기준 순서 -0.3으로 설정
- **뒤쪽 삽입**: 기준 순서 +0.3으로 설정
- **자동 정렬**: 수정 완료 후 1, 2, 3... 순서로 재정렬

### 자연어 처리 및 검증

#### 한국어 처리
- **지역명 추출**: 정규식 + GPT-4 조합으로 정확한 지역 파싱
- **여행 스타일 분류**: 가족여행, 혼자여행, 커플여행, 친구여행 자동 분류

#### JSON 검증 시스템
- **스키마 검증**: 생성된 JSON 구조 유효성 검사
- **재시도 메커니즘**: 최대 10회 재생성으로 품질 보장
- **필드 완성도 검사**: 필수 필드 누락 방지

### 설명 생성 엔진

#### 친근한 설명 생성
- **마크다운 형식**: 가독성 좋은 설명 포맷
- **개성 있는 톤**: "냥" 어미로 친근함 연출
- **컨셉 설명**: 여행 테마와 선택 이유 설명
- **200자 제한**: 간결하고 핵심적인 정보 제공

### AI 서비스 사용 예시

#### 여행 계획 생성 요청
```json
POST /ai/create
{
  "query": "강릉 2박 3일 가족여행 계획 짜줘"
}
```

#### 응답 예시
```json
{
  "attractions": [
    {
      "uniqueKey": "2025-05-15_1_56644",
      "attractionId": 56644,
      "attractionName": "경포해수욕장",
      "addr": "강원특별자치도 강릉시 창해로 514",
      "lat": 37.8056,
      "lng": 128.9019,
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-15",
      "attractionOrder": 1,
      "content": ""
    },
    {
      "uniqueKey": "2025-05-15_2_39001",
      "attractionId": 39001,
      "attractionName": "강릉 맛집거리",
      "addr": "강원특별자치도 강릉시 용강동",
      "categoryId": 39,
      "categoryName": "음식점",
      "date": "2025-05-15",
      "attractionOrder": 2
    }
  ],
  "description": "## 🏖️ 강릉 바다 여행 컨셉\n\n- **해변 중심** 가족 여행\n- **자연 경관**과 **맛집 투어** 조합\n- **아이들과 함께** 즐길 수 있는 코스\n\n경포해수욕장에서 시작해서 강릉의 바다 정취를 만끽할 수 있는 일정이냥! 가족 모두가 편안하게 즐길 수 있도록 구성했냥 🐱"
}
```

#### 일정 수정 요청
```json
POST /ai/update
{
  "query": "첫째 날에 오죽헌도 추가해줘",
  "plan": {
    "attractions": [
      /* 기존 일정 데이터 */
    ]
  }
}
```


---

**Happy Traveling! 🐱✈️**
