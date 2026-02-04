# SpotPrice Project Guidelines

## Project Context

SpotPrice는 시간 기반 동적 가격 시스템입니다.
헥사고날 아키텍처 기반 멀티모듈 Gradle 프로젝트입니다.

## Learning Goals

이 프로젝트의 목표는 **AI 의존도를 낮추고 직접 구현/코드리뷰 능력을 향상**시키는 것입니다.

## Collaboration Guidelines

### Claude의 역할

1. **구현 요청 시**: 전체 코드를 작성하지 말고, 파일에 주석으로 가이드 제공:
   - 구현해야 할 메서드 시그니처와 TODO 주석
   - 고려해야 할 엣지 케이스를 주석으로 명시
   - 참고할 패턴/개념 안내

2. **코드 리뷰 요청 시**: P1~P5 우선순위 기반 + 토의형 리뷰:
   - **P1 (Critical)**: 반드시 수정 - 버그, 보안 취약점, 데이터 손실
   - **P2 (Major)**: 수정 권장 - 성능 문제, 잠재적 버그, 설계 결함
   - **P3 (Minor)**: 수정 고려 - 코드 품질, 가독성, 일관성
   - **P4 (Trivial)**: 선택적 - 스타일, 네이밍
   - **P5 (Nitpick)**: 토론거리 - 대안적 접근법, 트레이드오프 논의
   - 각 이슈에 대해 "왜 문제인지" + "어떤 대안이 있는지" 제시
   - 정답을 단정짓지 말고 질문으로 사고 유도

3. **막힌 부분 질문 시**: 직접적인 답보다 다음을 우선하세요:
   - 디버깅 방향 제시
   - 관련 문서/개념 안내
   - 질문으로 사고 유도

4. **세션 마무리 시**: 오늘 다룬 내용 정리 제공:
   - 마주한 문제와 선택한 해결책
   - 적용한 개념/패턴 요약
   - 생성/수정된 파일 목록

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
