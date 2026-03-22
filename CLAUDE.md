# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

## 프로젝트 개요

Spring Boot 4.0.4 웹 애플리케이션. Java 17, Gradle 9.4 사용.
Redis 기반 세션 관리를 구현하는 테스트 프로젝트.

## 빌드 및 실행 명령어

- **빌드**: `./gradlew build`
- **실행**: `./gradlew bootRun`
- **전체 테스트**: `./gradlew test`
- **단일 테스트**: `./gradlew test --tests "com.example.demo.SomeTestClass.testMethod"`
- **클린 빌드**: `./gradlew clean build`

## 아키텍처

Spring Boot 기본 구조. 베이스 패키지: `com.example.demo`.

- `src/main/java/` — 애플리케이션 소스 코드
- `src/main/resources/application.properties` — Spring 설정
- `src/test/java/` — JUnit 5 테스트 (`@SpringBootTest`)

주요 의존성: `spring-boot-starter-web` (REST/웹), `spring-boot-starter-test` (JUnit Platform 테스트)
