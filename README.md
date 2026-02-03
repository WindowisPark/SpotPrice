# SpotPrice

> Time-based Reverse Auction Platform for Idle Spaces
>
> "시간이 가격과 점유권을 동시에 결정하는 시스템"

유휴 공간의 시간 기반 역경매 플랫폼입니다. 시간이 지남에 따라 가격이 자동으로 하락하며, 먼저 결제한 사람이 공간을 사용할 수 있습니다.

---

## 핵심 개념

### Spot (공간)
무인 파티룸, 스터디룸 등 유휴 시간대를 판매하려는 공간

### Offer (판매 단위)
특정 공간의 특정 시간대를 판매하는 단위
- 재고는 항상 1 (선착순 1명)
- 시간이 지나면 가격이 자동 하락

### 가격 정책
```
price(t) = base_price - (base_price - min_price) * (elapsed / total)
```
- 현재 가격은 DB에 저장하지 않음
- 모든 가격은 서버가 실시간 계산
- 10원 단위 내림 반올림

---

## 설계 철학

| 원칙 | 설명 |
|------|------|
| 가격은 저장하지 않는다 | 모든 가격은 `f(now)`로 계산 |
| 선점(Hold) 없음 | 결제 순간에만 락 획득 |
| 결제 승자는 단 1명 | 동시 결제 시 DB 조건 업데이트로 보장 |
| 정합성 기준은 서버 | 클라이언트 가격은 참고값 |

---

## 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 3.5
- **Database**: MySQL 8.0 / H2 (개발)
- **Architecture**: Hexagonal (Ports & Adapters)

---

## 프로젝트 구조

```
spotprice/
├─ spotprice-domain        # 순수 도메인 모델
├─ spotprice-application   # 유즈케이스, 포트 정의
├─ spotprice-infrastructure# 어댑터 구현 (DB, 외부 서비스)
├─ spotprice-api           # REST API (Spring Boot)
└─ docs/                   # 문서
   ├─ POLICY.md            # 정책 명세
   ├─ ERD.md               # ERD
   ├─ DDL.md               # DDL
   ├─ API_CONTRACT.md      # API 명세
   └─ TEST_SCENARIOS.md    # 테스트 시나리오
```

---

## 시작하기

### 요구사항
- JDK 21+
- Gradle 8.x

### 빌드
```bash
./gradlew build
```

### 실행
```bash
./gradlew :spotprice-api:bootRun
```

### 테스트
```bash
./gradlew test
```

---

## API 흐름

```
1. 조회: GET /api/v1/offers/{id}
   └─ currentPrice, quotedAt 획득

2. 주문: POST /api/v1/orders
   └─ clientPrice <= serverPrice 검증
   └─ PENDING 상태 주문 생성

3. 결제: POST /api/v1/orders/{id}/pay
   └─ 락 획득 → 상태 검증 → SOLD → AccessGrant 발급
```

### 동시성 처리
```
User A ─┬─ 결제 요청 ─→ 락 획득 ─→ SOLD ─→ 성공
        │
User B ─┴─ 결제 요청 ─→ 락 대기 ─→ 이미 SOLD ─→ 409 Conflict
```

---

## 문서

| 문서 | 설명 |
|------|------|
| [POLICY.md](docs/POLICY.md) | 비즈니스 정책 명세 (최상위 문서) |
| [ERD.md](docs/ERD.md) | 데이터 모델 |
| [DDL.md](docs/DDL.md) | DDL 스크립트 |
| [API_CONTRACT.md](docs/API_CONTRACT.md) | REST API 명세 |
| [TEST_SCENARIOS.md](docs/TEST_SCENARIOS.md) | 테스트 시나리오 |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | 아키텍처 개요 |

---

## MVP 범위

### 포함
- LINEAR 가격 함수
- 동시성 제어 (분산락 + DB 조건 업데이트)
- 멱등성 보장 (idempotency_key)
- AccessGrant (QR/PIN)

### 제외 (v2 예정)
- Hold / 예약 선점
- EXPONENTIAL 가격 함수
- 외부 PG 연동
- 복잡한 환불/정산

---

## 라이선스

MIT License
