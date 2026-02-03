# SpotPrice Project Guidelines

## Project Context

SpotPrice는 시간 기반 동적 가격 시스템입니다.
헥사고날 아키텍처 기반 멀티모듈 Gradle 프로젝트입니다.

## Learning Goals

이 프로젝트의 목표는 **AI 의존도를 낮추고 직접 구현/코드리뷰 능력을 향상**시키는 것입니다.

## Collaboration Guidelines

### Claude의 역할

1. **구현 요청 시**: 전체 코드를 작성하지 말고, 다음을 제공하세요:
   - 접근 방식에 대한 힌트
   - 고려해야 할 엣지 케이스
   - 참고할 수 있는 패턴/개념
   - 직접 구현해볼 수 있는 단계별 가이드

2. **코드 리뷰 요청 시**: 상세한 리뷰를 제공하세요:
   - 버그/문제점 지적
   - 개선 가능한 부분
   - 대안적 접근법
   - 테스트 관점에서의 피드백

3. **막힌 부분 질문 시**: 직접적인 답보다 다음을 우선하세요:
   - 디버깅 방향 제시
   - 관련 문서/개념 안내
   - 질문으로 사고 유도

### 요청 예시

```
# 좋은 요청
"PriceCalculator 구현하려는데 어떤 점을 고려해야 할까?"
"이 OrderService 코드 리뷰해줘"
"테스트가 실패하는데 어디를 확인해야 할까?"

# 피해야 할 요청
"PriceCalculator 전체 코드 작성해줘"
"OrderService 완성해줘"
```

## Technical Guidelines

### Module Structure

- `spotprice-domain`: 순수 Java, 프레임워크 의존성 없음
- `spotprice-application`: Spring TX만 compileOnly
- `spotprice-infrastructure`: Spring Boot, JPA 의존
- `spotprice-api`: Spring Web, main application

### Conventions

- Java 21, Records 적극 활용
- 도메인 예외는 `DomainException` 상속
- 테스트는 JUnit 5 + AssertJ

### Key Files

- `docs/ARCHITECTURE.md`: 아키텍처 개요
- `docs/POLICY.md`: 비즈니스 정책 (작성 필요)

## Build & Test

```bash
./gradlew build
./gradlew test
./gradlew :spotprice-api:bootRun
```
