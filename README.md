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
