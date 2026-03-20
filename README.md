# 📔 Sentiment Analysis Diary

> 일기를 작성하면 GPT가 감정을 분석하고 감정 점수와 핵심 키워드를 자동으로 기록해주는 감정 분석 일기 서비스

---

## 프로젝트 소개

### 한 줄 설명

일기 내용을 GPT API로 분석하여 감정 점수(-10 ~ +10)와 핵심 키워드를 자동으로 추출·저장하는 백엔드 서비스입니다.

### 배경 및 목적

매일의 감정을 글로 기록하는 것은 정서적으로 큰 도움이 됩니다. 하지만 직접 감정을 분류하고 돌아보는 일은 번거롭습니다. 이 프로젝트는 OpenAI GPT API를 활용해 일기 텍스트를 자동으로 감정 분석하고, 감정 점수와 핵심 명사 키워드를 추출해 저장함으로써 사용자가 자신의 감정 흐름을 추적할 수 있도록 돕는 것을 목표로 합니다.

---

## 주요 기능

- **일기 CRUD**: 일기 작성, 조회(단일/날짜별/기간별/페이지), 수정, 삭제
- **GPT 기반 감정 분석**: 일기 저장 및 수정 시 GPT가 내용을 분석해 -10 ~ +10 사이의 감정 점수를 자동 산출
- **키워드 자동 추출**: GPT를 통해 일기 내 중요도 높은 핵심 명사를 추출
- **기간별 키워드 통계**: 특정 기간 내 일기의 키워드 빈도를 집계해 반환
- **회원 관리**: 이메일 기반 일반 회원가입 / Google OAuth2 소셜 로그인 지원
- **JWT 인증**: Access Token + Refresh Token 이중 토큰 방식으로 보안 강화
- **이메일 중복 검증**: 회원가입 시 이메일·닉네임 중복 확인 API 제공
- **API 문서화**: Swagger(Springfox 3.0)를 통한 API 명세 자동 제공

---

## 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_11-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_2.7.7-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)

### AI / API
![OpenAI](https://img.shields.io/badge/OpenAI_GPT-412991?style=flat-square&logo=openai&logoColor=white)

### Database
![MySQL](https://img.shields.io/badge/MySQL_8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=flat-square&logo=amazonrds&logoColor=white)

### 인증 / 보안
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/Google_OAuth2-4285F4?style=flat-square&logo=google&logoColor=white)

### 인프라 / DevOps
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

### 문서화 / 유틸
![Swagger](https://img.shields.io/badge/Swagger_3.0-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![MapStruct](https://img.shields.io/badge/MapStruct_1.5-red?style=flat-square)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)

---

## 시스템 아키텍처

```
[ Client ]
    │
    ▼
[ AWS EC2 - Docker Container (Spring Boot :8080) ]
    │
    ├── Spring Security (JWT 인증 필터 / OAuth2 로그인)
    │       └── /auth/login → JWT 발급 (Access Token 120분 / Refresh Token 20일)
    │
    ├── DiaryController  → DiaryService
    │       ├── 일기 저장/수정 시 ChatgptService 호출
    │       │       ├── findEmotion()  : GPT에 "-10~+10 감정점수화" 프롬프트 전송
    │       │       └── findKeywords() : GPT에 핵심 명사 추출 프롬프트 전송
    │       └── DiaryRepository (JPA)
    │
    ├── MemberController → MemberService
    │       ├── 일반 회원가입 (이메일/비밀번호, BCrypt 암호화)
    │       ├── Google OAuth2 소셜 로그인
    │       └── MemberRepository (JPA)
    │
    └── AWS RDS (MySQL 8.0)

[ CI/CD Pipeline ]
  GitHub Push (main) → GitHub Actions
      → Gradle 빌드 → Docker 이미지 빌드 (DockerHub 푸시)
      → AWS EC2 SSH 접속 → 기존 컨테이너 교체 배포
```

---

## API 명세

### 다이어리 API (`/api/diary`)

| HTTP 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| `POST` | `/api/diary` | 일기 생성 (GPT 감정분석 자동 실행) | 필요 |
| `GET` | `/api/diary/{diary-id}` | 일기 단일 조회 | 필요 |
| `GET` | `/api/diary/date?createdAt={yyyy-MM-dd}` | 날짜별 일기 조회 | 필요 |
| `GET` | `/api/diary?page={n}&size={n}` | 내 일기 목록 페이지 조회 | 필요 |
| `GET` | `/api/diary/term?startDate={yyyy-MM-dd}&endDate={yyyy-MM-dd}` | 기간별 일기 + 키워드 통계 조회 | 필요 |
| `PATCH` | `/api/diary/{diary-id}` | 일기 수정 (GPT 재분석 실행) | 필요 |
| `DELETE` | `/api/diary/{diary-id}` | 일기 삭제 | 필요 |

### 회원 API (`/api/members`)

| HTTP 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| `POST` | `/api/members` | 회원가입 | 불필요 |
| `POST` | `/api/members/check` | 이메일/닉네임 중복 확인 | 불필요 |
| `GET` | `/api/members/login` | 로그인 회원 정보 조회 | 필요 |
| `GET` | `/api/members/search?nickname={nickname}` | 닉네임으로 회원 검색 | 필요 |
| `PATCH` | `/api/members/patchGoogleMember` | Google 첫 로그인 시 닉네임 설정 | 필요 |
| `GET` | `/api/members/getGoogleMember` | Google 로그인 기존 사용자 정보 조회 | 필요 |
| `DELETE` | `/api/members/{member-id}` | 회원 탈퇴 | 필요 |

### 인증 API

| HTTP 메서드 | 경로 | 설명 |
|---|---|---|
| `POST` | `/auth/login` | 이메일/비밀번호 로그인 (JWT 발급) |
| - | `/oauth2/authorization/google` | Google OAuth2 로그인 진입점 |

> 상세 API 명세는 서버 실행 후 `http://localhost:8080/swagger-ui/index.html` 에서 확인 가능합니다.

---

## 트러블슈팅 및 기술적 고민

### 1. GPT API 응답에서 숫자 파싱 안정성 확보

**문제**: GPT에게 감정 점수를 요청하면 "감정 점수는 +3입니다." 와 같이 자연어가 섞인 응답이 올 수 있어, 단순 파싱 시 `NumberFormatException`이 발생할 위험이 있었습니다.

**해결**: 프롬프트를 "문단 전체를 -10 ~ +10 사이로 감정점수화해서 숫자만 반환"하도록 명확히 구성하고, `DiaryService`의 `findEmotion()` 메서드에서 응답 파싱 로직을 별도로 분리하여 안정적으로 처리했습니다.

---

### 2. JWT Stateless 인증과 Refresh Token 전략

**고민**: REST API 특성상 세션을 사용하지 않는 Stateless 구조를 택했지만, Access Token만 사용하면 만료 시 매번 재로그인해야 하는 UX 문제가 있었습니다.

**해결**: Access Token(120분) + Refresh Token(20일) 이중 토큰 방식을 채택했습니다. Refresh Token은 DB에 저장하고, Access Token 만료 시 Refresh Token 검증 후 재발급하는 구조로 설계했습니다.

---

### 3. Google OAuth2와 일반 로그인 회원의 통합 관리

**고민**: Google 소셜 로그인 유저와 일반 이메일 가입 유저를 동일한 `Member` 엔티티로 관리하면서, 각각의 로그인 흐름을 분리해야 했습니다.

**해결**: `Member` 엔티티에 `google` boolean 필드를 추가해 가입 유형을 구분했습니다. Google 유저는 최초 로그인 시 닉네임이 없으므로 별도의 `patchGoogleMember` API를 제공하고, `Oauth2MemberSuccessHandler`에서 JWT를 발급하는 흐름을 분리 구현했습니다.

---

## 실행 방법

### 사전 요구사항

- Java 11
- Docker & Docker Compose
- MySQL 8.0 (또는 AWS RDS)
- OpenAI API Key
- Google OAuth2 Client ID / Secret

### 환경 변수 설정

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://{DB_HOST}:3306/{DB_NAME}
SPRING_DATASOURCE_USERNAME={DB_USERNAME}
SPRING_DATASOURCE_PASSWORD={DB_PASSWORD}
GOOGLE_CLIENT_ID={GOOGLE_OAUTH_CLIENT_ID}
GOOGLE_CLIENT_SECRET={GOOGLE_OAUTH_CLIENT_SECRET}
JWT_SECRET_KEY={JWT_SECRET_KEY}
OPENAI_KEY={OPENAI_API_KEY}
```

### Docker Compose로 실행 (권장)

```bash
# 1. 레포지토리 클론
git clone https://github.com/Aru-slave/sentiment-analysis-diary.git
cd sentiment-analysis-diary

# 2. Gradle 빌드
./gradlew build -x test

# 3. Docker Compose 실행
docker-compose up -d
```

서버가 실행되면 `http://localhost:8080`으로 접근 가능합니다.

### API 문서 확인

```
http://localhost:8080/swagger-ui/index.html
```

---

## 프로젝트 구조

```
src/main/java/com/sentimentdiary/demo/
├── DemoApplication.java
├── auditable/                    # JPA Auditing (생성일/수정일 자동화)
├── auth/                         # 인증 모듈
│   ├── RefreshToken/             # Refresh Token 엔티티 및 관리
│   ├── filter/                   # JWT 인증/검증 필터
│   ├── handler/                  # 로그인 성공/실패 핸들러
│   └── jwt/                      # JWT 토큰 생성 및 검증
├── config/
│   ├── SecurityConfiguration.java
│   └── SwaggerConfig.java
├── diary/                        # 일기 도메인
│   ├── Diary.java
│   ├── DiaryController.java
│   ├── DiaryService.java         # GPT 연동 포함
│   └── DiaryRepository.java
├── member/                       # 회원 도메인
│   ├── Member.java
│   ├── MemberController.java
│   ├── MemberService.java
│   └── MemberRepository.java
└── exception/                    # 전역 예외 처리
```
