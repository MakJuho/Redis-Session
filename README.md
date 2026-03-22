# Redis-Session

Spring Boot 4.0.4 기반 Redis 세션 관리 테스트 프로젝트

## 기술 스택

- Java 17
- Spring Boot 4.0.4
- Spring Session Data Redis
- Gradle 9.4

## 실행 방법

### 1. Redis 실행

```bash
# Docker
docker run -d -p 6379:6379 --name redis redis

# 또는 Homebrew
brew install redis
redis-server
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 3. 브라우저 접속

```
http://localhost:8080
```

## 아키텍처

### 세션 동작 흐름

```
┌──────────┐         ┌──────────────────┐         ┌─────────┐
│  Client  │         │   Spring Boot    │         │  Redis   │
│ (Browser)│         │                  │         │          │
└────┬─────┘         └────────┬─────────┘         └────┬─────┘
     │                        │                        │
     │  1. POST /session      │                        │
     │  (key/value 전송)      │                        │
     ├───────────────────────>│                        │
     │                        │  2. 세션 생성/조회      │
     │                        │  session.setAttribute()│
     │                        ├───────────────────────>│
     │                        │                        │ 3. Hash 구조로 저장
     │                        │                        │ spring:session:sessions:{id}
     │                        │  4. 저장 완료           │
     │                        │<───────────────────────┤
     │  5. 응답 + Set-Cookie  │                        │
     │  SESSION={Base64(id)}  │                        │
     │<───────────────────────┤                        │
     │                        │                        │
     │  6. GET /session?key=  │                        │
     │  Cookie: SESSION=xxx   │                        │
     ├───────────────────────>│                        │
     │                        │  7. 쿠키의 세션ID로     │
     │                        │  Redis에서 조회         │
     │                        ├───────────────────────>│
     │                        │  8. 세션 데이터 반환     │
     │                        │<───────────────────────┤
     │  9. 응답 (value)       │                        │
     │<───────────────────────┤                        │
     │                        │                        │
```

### Redis 세션 저장 구조

하나의 세션은 Redis Hash로 저장되며, 여러 key/value를 하나의 세션 안에 담습니다.

```
Redis Key: spring:session:sessions:2b46ac66-9f89-4aaa-a413-558c41269e36

┌─────────────────────────┬──────────────────────┐
│ Hash Field              │ Value                │
├─────────────────────────┼──────────────────────┤
│ creationTime            │ 1774171048483        │
│ lastAccessedTime        │ 1774171048483        │
│ maxInactiveInterval     │ 1800 (30분)          │
│ sessionAttr:name        │ "makjuho"            │
│ sessionAttr:role        │ "developer"          │
│ sessionAttr:age         │ "30"                 │
└─────────────────────────┴──────────────────────┘
```

### 왜 Redis를 세션 저장소로 사용하는가

| 기본 HttpSession (Tomcat) | Redis Session |
|--------------------------|---------------|
| 서버 메모리(JVM Heap)에 저장 | 외부 Redis에 저장 |
| 서버 재시작 시 세션 소멸 | 서버 재시작해도 세션 유지 |
| 서버가 여러 대면 세션 공유 불가 | 여러 서버가 동일 세션 공유 가능 |
| Scale-up만 가능 | Scale-out 가능 |

```
[Scale-out 환경에서의 세션 공유]

┌──────────┐     ┌──────────┐     ┌──────────┐
│ Server A │     │ Server B │     │ Server C │
└────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │
     └────────────────┼────────────────┘
                      │
                ┌─────┴─────┐
                │   Redis   │
                │ (세션 저장) │
                └───────────┘
```

### 프로젝트 구조

```
com.example.demo/
├── DemoApplication.java              # Spring Boot 진입점
├── config/
│   └── RedisSessionConfig.java       # @EnableRedisHttpSession + JSON 직렬화 설정
└── controller/
    └── SessionController.java        # 세션 CRUD REST API
```

- **RedisSessionConfig**: Spring Session이 Tomcat 기본 세션 대신 Redis를 사용하도록 설정. `GenericJacksonJsonRedisSerializer`로 직렬화하여 Redis에서 JSON으로 값 확인 가능.
- **SessionController**: `HttpSession`을 주입받아 세션 속성을 조작. Spring Session이 내부적으로 Redis와 동기화.

## API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/session` | 세션에 key/value 저장 |
| GET | `/session?key={key}` | 세션 값 조회 |
| DELETE | `/session` | 세션 무효화 |

### 요청 예시

```bash
# 저장
curl -c cookies.txt -X POST http://localhost:8080/session \
  -H "Content-Type: application/json" \
  -d '{"key":"name","value":"makjuho"}'

# 조회
curl -b cookies.txt "http://localhost:8080/session?key=name"

# 삭제
curl -b cookies.txt -X DELETE http://localhost:8080/session
```

### Redis에서 세션 확인

```bash
# 세션 키 목록
redis-cli keys "spring:session:sessions:*"

# 세션 값 조회
redis-cli hgetall "spring:session:sessions:<세션ID>"
```

## 테스트

```bash
./gradlew test
```
