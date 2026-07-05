# 프로젝트 세팅 문서

## 목차
1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [인증 방침](#3-인증-방침)
4. [패키지 구조](#4-패키지-구조)
5. [global 패키지 상세](#5-global-패키지-상세)
6. [설정 파일(yml) 구조](#6-설정-파일yml-구조)
7. [로컬 개발 환경 세팅](#7-로컬-개발-환경-세팅)
8. [EC2 배포 환경 세팅](#8-ec2-배포-환경-세팅)
9. [환경변수 목록](#9-환경변수-목록)
10. [도메인 패키지 개발 가이드](#10-도메인-패키지-개발-가이드)

---

## 1. 프로젝트 개요

연동된 사람과 실시간으로 위치·사진을 공유하며 하루의 동선을 기록하고,
원하는 시점에 **"오늘의 동선 카드"** 로 뽑아 SNS에 공유하는 웹앱 (세미 해커톤 프로젝트).

### MVP 우선순위

| 순위 | 기능 | 상태 |
|------|------|------|
| 1 | 사용자 실시간 위치 지도 표시 | 개발 대상 |
| 2 | 사진 업로드 (EXIF에서 위치 추출, 위치 핀 표시) | 개발 대상 |
| 3 | 요약 카드 발급 (버튼 누른 시점까지의 동선+사진 스냅샷) | 개발 대상 |
| 4 | 타인/커플 연동 | 추후 확장 (보류) |

---

## 2. 기술 스택

| 분류 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.5.16 |
| 빌드 | Gradle 8.14.5 |
| DB | MySQL 8.x (AWS EC2에 직접 설치, RDS 미사용) |
| ORM | Spring Data JPA + Hibernate 6.6 |
| API 문서 | Swagger / springdoc-openapi 2.8.17 |
| EXIF 추출 | metadata-extractor 2.19.0 |
| 파일 저장 | EC2 로컬 디스크 (`uploads/` 폴더), S3 미사용 |
| 실시간 위치 | 브라우저 Geolocation API + 폴링 방식 (3초, WebSocket 아님) |
| 프론트 | React (Vercel 또는 Netlify 배포 예정) |
| 유틸 | Lombok, Spring Validation |

---

## 3. 인증 방침

> 로그인/인증 기능은 해커톤 평가 항목 제외 → 최소한으로만 구현

- JWT, Spring Security **사용하지 않음**
- 최초 접속 시 닉네임 입력 → 서버가 `userId` 발급
- 이후 클라이언트가 `userId`를 들고 있다가 **모든 API 요청에 직접 실어 보냄**
  - GET 요청 → 쿼리 파라미터 (`?userId=1`)
  - POST/PATCH 요청 → body 필드 (`"userId": 1`)
- 서버는 넘어온 `userId`를 별도 검증 없이 신뢰 (데모 목적)

```
POST /users/login  { "nickname": "이름" }
    ↓
응답: { "userId": 1 }
    ↓
이후 모든 요청에 userId를 파라미터/body에 포함
```

**Swagger 테스트**: Authorize 버튼 없음. 각 API에서 `userId` 파라미터/body에 직접 값 입력 후 실행.

---

## 4. 패키지 구조

```
com.likelion_collb
├── LikelionCollbApplication.java       # 메인 클래스 (@EnableJpaAuditing 포함)
├── global
│   ├── config
│   │   ├── JacksonConfig.java          # LocalDateTime ISO-8601 직렬화
│   │   ├── StaticResourceConfig.java   # /images/** 정적 파일 서빙
│   │   ├── SwaggerConfig.java          # Swagger UI 설정
│   │   └── WebConfig.java              # CORS 설정
│   ├── entity
│   │   └── BaseTimeEntity.java         # createdAt / updatedAt 공통 Auditing
│   ├── exception
│   │   ├── CommonErrorCode.java        # 공통 에러코드 enum
│   │   ├── CustomException.java        # 도메인 에러 throw용 RuntimeException
│   │   ├── ErrorCode.java              # 에러코드 인터페이스
│   │   └── GlobalExceptionHandler.java # 전역 예외 처리
│   ├── response
│   │   ├── BaseResponse.java           # 공통 API 응답 래퍼
│   │   └── PageResponse.java           # 페이지 응답 래퍼 (추후 사용)
│   └── HealthCheckController.java      # 배포 확인용 임시 컨트롤러
└── domain
    ├── user/
    ├── photo/
    ├── track/
    └── card/
```

---

## 5. global 패키지 상세

### 5-1. Config

#### `WebConfig.java` — CORS 설정
- `cors.allowed-origins` 프로퍼티를 콤마(`,`) 구분으로 파싱해 다중 도메인 허용
- 허용 메서드: GET, POST, PUT, PATCH, DELETE, OPTIONS
- `allowCredentials(true)` — 추후 쿠키 기반 기능 확장 대비
- **주의:** 프론트가 HTTPS 배포되면 백엔드도 HTTPS 필요 (Mixed Content 이슈)

```yaml
# 단일 도메인 (application-local.yml)
cors:
  allowed-origins: http://localhost:3000

# 다중 도메인 (쉼표 구분)
cors:
  allowed-origins: https://front.vercel.app,https://www.front.com
```

#### `JacksonConfig.java` — 날짜/시간 직렬화
- `JavaTimeModule` 등록 → `LocalDateTime` 직렬화 가능
- `WRITE_DATES_AS_TIMESTAMPS` 비활성화 → 배열(`[2026,7,5,...]`) 대신 ISO-8601 문자열(`"2026-07-05T03:27:00"`) 반환
- `Photo.takenAt`, `TrackPoint.recordedAt` 등 시간 필드가 프론트와 주고받을 때 적용됨

#### `StaticResourceConfig.java` — 업로드 이미지 정적 서빙
- `/images/**` URL 요청을 `file.upload-dir` 경로로 매핑
- `@PostConstruct`로 앱 시작 시 업로드 디렉토리 자동 생성 (EC2 첫 배포 시 폴더 없음 방지)
- 이미지 접근 URL 형태: `http://서버주소:8080/images/파일명.jpg`

```yaml
# 로컬 (application-local.yml)
file:
  upload-dir: uploads/

# EC2 (application-prod.yml)
file:
  upload-dir: ${FILE_UPLOAD_DIR:/home/ubuntu/app/uploads/}
```

#### `SwaggerConfig.java` — API 문서
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- API Docs: `http://localhost:8080/v3/api-docs`
- 인증 없음 — 각 API에서 `userId` 파라미터/body에 직접 입력 후 테스트

---

### 5-2. Entity

#### `BaseTimeEntity.java` — 공통 시간 Auditing
도메인 엔티티가 상속해서 `createdAt`, `updatedAt`을 자동 관리.

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseTimeEntity {
    // createdAt, updatedAt 자동 주입
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `createdAt` | `LocalDateTime` | 생성 시각 (수정 불가) |
| `updatedAt` | `LocalDateTime` | 마지막 수정 시각 |

- `@EnableJpaAuditing`은 `LikelionCollbApplication.java`에 이미 추가됨

---

### 5-3. Exception

#### `ErrorCode.java` — 에러코드 인터페이스
```java
public interface ErrorCode {
    String getCode();       // 에러 식별 코드 (예: "COMMON_400")
    String getMessage();    // 사용자에게 보여줄 메시지
    HttpStatus getStatus(); // HTTP 상태코드
}
```

#### `CommonErrorCode.java` — 공통 에러코드

| 상수 | 코드 | 메시지 | HTTP Status |
|------|------|--------|-------------|
| `INVALID_REQUEST` | COMMON_400 | 잘못된 요청입니다. | 400 |
| `UNAUTHORIZED` | AUTH_401 | 로그인이 필요합니다. | 401 |
| `FORBIDDEN` | AUTH_403 | 접근할 수 없는 항목입니다. | 403 |
| `NOT_FOUND` | COMMON_404 | 정보를 찾을 수 없습니다. | 404 |
| `ALREADY_PROCESSED` | COMMON_409 | 이미 처리된 요청입니다. | 409 |
| `INTERNAL_SERVER_ERROR` | COMMON_500 | 서버 오류가 발생했습니다. | 500 |

도메인별 에러코드는 각 도메인 패키지에서 `ErrorCode` 인터페이스를 구현해 별도 정의:
```java
// 예시: domain/photo/exception/PhotoErrorCode.java
@Getter
@RequiredArgsConstructor
public enum PhotoErrorCode implements ErrorCode {
    PHOTO_NOT_FOUND("PHOTO_404", "사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_FILE_TYPE("PHOTO_400", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
```

#### `CustomException.java` — 커스텀 예외 throw
```java
// 기본 사용
throw new CustomException(CommonErrorCode.NOT_FOUND);

// 추가 데이터 포함
throw new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND, photoId);
```

#### `GlobalExceptionHandler.java` — 전역 예외 처리

| 처리 대상 | 동작 |
|-----------|------|
| `CustomException` | `errorCode` 기반으로 HTTP 상태 + `BaseResponse.fail()` 반환 |
| `MethodArgumentNotValidException` | `@Valid` 실패 시 필드별 에러 목록 반환 |
| `Exception` | 미처리 예외 → 500 반환 + 로그 기록 |

> `localhost:8080` 루트 접근 시 매핑된 엔드포인트가 없어 `COMMON_500` 응답이 오는 것은 정상 동작.

---

### 5-4. Response

#### `BaseResponse<T>` — 공통 API 응답 래퍼

```json
{
  "success": true,
  "code": "COMMON_200",
  "message": "요청에 성공했습니다.",
  "data": { ... }
}
```

| 정적 팩토리 메서드 | 설명 |
|-------------------|------|
| `BaseResponse.success(data)` | 성공, 기본 메시지 |
| `BaseResponse.success(data, message)` | 성공, 커스텀 메시지 |
| `BaseResponse.success(code, message, data)` | 성공, 코드·메시지 모두 커스텀 |
| `BaseResponse.fail(errorCode)` | 실패, data = null |
| `BaseResponse.fail(errorCode, data)` | 실패, 추가 데이터 포함 |

#### `PageResponse<T>` — 페이지 응답 래퍼 (추후 사용)

```java
return BaseResponse.success(PageResponse.from(photoPage, PhotoResponse::from));
```

---

## 6. 설정 파일(yml) 구조

### 파일 분리 구조

```
src/main/resources/
├── application.yml          # 공통 설정 (환경 무관)
├── application-local.yml    # 로컬 개발용 (gitignore 처리됨)
└── application-prod.yml     # EC2 배포용 (환경변수로 주입)
```

### `application.yml` (공통)

```yaml
spring:
  application:
    name: likelion-collb
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}

  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB

server:
  port: 8080
```

### `application-local.yml` (로컬 개발용)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/likelion_collb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: 본인_비밀번호
    driver-class-name: com.mysql.cj.jdbc.Driver

file:
  upload-dir: uploads/

cors:
  allowed-origins: http://localhost:3000
```

> `application-local.yml`은 `.gitignore`에 포함 → **팀원마다 직접 생성** 필요

### `application-prod.yml` (EC2 배포용)

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

file:
  upload-dir: ${FILE_UPLOAD_DIR:/home/ubuntu/app/uploads/}

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

---

## 7. 로컬 개발 환경 세팅

### 사전 요구사항
- Java 17 (Temurin 권장)
- MySQL 8.x
- IntelliJ IDEA

### 순서

**1. MySQL DB 생성**
```sql
CREATE DATABASE likelion_collb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**2. `application-local.yml` 생성**

`src/main/resources/` 아래에 직접 생성 (git에 포함되지 않음):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/likelion_collb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: 본인_MySQL_비밀번호
    driver-class-name: com.mysql.cj.jdbc.Driver

file:
  upload-dir: uploads/

cors:
  allowed-origins: http://localhost:3000
```

**3. 애플리케이션 실행**

IntelliJ에서 `LikelionCollbApplication` 실행 또는:
```bash
./gradlew bootRun
```

**4. 동작 확인**
- 헬스체크: `GET http://localhost:8080/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 8. EC2 배포 환경 세팅

### EC2 초기 서버 세팅

**Java 17 설치**
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
java -version
```

**MySQL 설치 및 설정**
```bash
sudo apt install -y mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
sudo mysql
```
```sql
CREATE DATABASE likelion_collb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'collb_user'@'localhost' IDENTIFIED BY '비밀번호';
GRANT ALL PRIVILEGES ON likelion_collb.* TO 'collb_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 환경변수 설정

`~/.bashrc`에 추가:
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://localhost:3306/likelion_collb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
export DB_USERNAME=collb_user
export DB_PASSWORD=비밀번호
export FILE_UPLOAD_DIR=/home/ubuntu/app/uploads/
export CORS_ALLOWED_ORIGINS=https://프론트배포주소.vercel.app
```
```bash
source ~/.bashrc
```

### 배포 및 실행

```bash
# 빌드 (로컬에서)
./gradlew bootJar

# jar 파일 EC2로 전송
scp build/libs/*.jar ubuntu@EC2주소:/home/ubuntu/app/app.jar

# EC2에서 실행
chmod +x deploy/run.sh
bash deploy/run.sh
```

### `deploy/run.sh` 스크립트

```bash
#!/bin/bash
PID=$(pgrep -f 'app.jar')
if [ -n "$PID" ]; then
  echo "기존 프로세스 종료: $PID"
  kill -9 $PID
fi

nohup java -jar -Dspring.profiles.active=prod app.jar > app.log 2>&1 &
echo "서버 시작됨. 로그: app.log"
```

로그 확인:
```bash
tail -f app.log
```

---

## 9. 환경변수 목록

### 로컬 개발 — `application-local.yml`에 직접 작성 (환경변수 불필요)

| 항목 | yml 키 | 예시 값 |
|------|--------|---------|
| DB URL | `spring.datasource.url` | `jdbc:mysql://localhost:3306/likelion_collb?...` |
| DB 유저명 | `spring.datasource.username` | `root` |
| DB 비밀번호 | `spring.datasource.password` | 본인 비밀번호 |
| 업로드 경로 | `file.upload-dir` | `uploads/` |
| CORS 허용 도메인 | `cors.allowed-origins` | `http://localhost:3000` |

### EC2 배포 — 서버에 환경변수로 주입 필수

| 환경변수명 | 예시 값 | 필수 여부 |
|------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | `prod` | 필수 |
| `DB_URL` | `jdbc:mysql://localhost:3306/likelion_collb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8` | 필수 |
| `DB_USERNAME` | `collb_user` | 필수 |
| `DB_PASSWORD` | `****` | 필수 |
| `FILE_UPLOAD_DIR` | `/home/ubuntu/app/uploads/` | 선택 (기본값 있음) |
| `CORS_ALLOWED_ORIGINS` | `https://your-front.vercel.app` | 선택 (기본값 있음) |

---

## 10. 도메인 패키지 개발 가이드

새 도메인(`user`, `photo`, `track`, `card`) 추가 시 아래 패턴을 따를 것.

### 패키지 구조 예시

```
domain/photo/
├── controller/PhotoController.java
├── service/PhotoService.java
├── repository/PhotoRepository.java
├── entity/Photo.java
├── dto/
│   ├── PhotoUploadRequest.java
│   └── PhotoResponse.java
└── exception/PhotoErrorCode.java
```

### 엔티티 작성 시 BaseTimeEntity 상속

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private LocalDateTime takenAt;
    // ...
}
```

### 도메인 에러코드 작성

```java
@Getter
@RequiredArgsConstructor
public enum PhotoErrorCode implements ErrorCode {
    PHOTO_NOT_FOUND("PHOTO_404", "사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_FILE_TYPE("PHOTO_400", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
```

### 컨트롤러 응답 패턴

```java
// 단건 조회
return ResponseEntity.ok(BaseResponse.success(photoResponse));

// 목록 조회 (페이지)
return ResponseEntity.ok(BaseResponse.success(PageResponse.from(photoPage, PhotoResponse::from)));

// 에러 throw
throw new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND);
```

### 파일 확장자 검증

확장자 검증은 **Service단**에서 처리:
```java
private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

String extension = /* 파일명에서 추출 */;
if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
    throw new CustomException(PhotoErrorCode.INVALID_FILE_TYPE);
}
```

### 이미지 저장 경로 규칙

- 저장 경로: `{file.upload-dir}/{UUID}.{확장자}` (예: `uploads/a3f2...jpg`)
- 응답 URL: `http://서버주소:8080/images/{UUID}.{확장자}`
- `StaticResourceConfig`가 `/images/**` → `uploads/` 매핑을 자동 처리함

### 위치 저장 전략

| 테이블 | 저장 방식 | 주기 | 용도 |
|--------|-----------|------|------|
| `LiveLocation` | UPSERT (덮어쓰기) | 3초 | 현재 위치 마커 |
| `TrackPoint` | INSERT (누적) | 3초 | 동선 선 그리기 |

- **API는 `POST /locations` 하나로 통합** — 한 번의 요청으로 LiveLocation UPSERT + TrackPoint INSERT 동시 처리
- Service 메서드에 `@Transactional` 적용
- TrackPoint는 거리 필터 없이 **무조건 INSERT** (해커톤 데모 특성상 제자리 테스트 고려)
- 프론트는 TrackPoint 좌표 배열을 Kakao Maps Polyline으로 연결해서 선 표시
