# Domain Relationship & Aggregate Boundaries (SpotPrice v1)

## 0) 목적
SpotPrice v1의 도메인 간 관계(참조 방식), Aggregate 경계, 소유/책임, 불변식을 고정한다.  
본 문서는 멀티 모듈 모놀리식 구조에서 **도메인 결합도 폭발을 방지**하고, 향후 **MSA 분리 가능성**을 구조적으로 확보하기 위한 기준 문서다.

---

## 1) Aggregate 정의 (최소 3개)

### 1.1 Offer Aggregate (판매 단위)
- 책임
    - 판매 단위(Offer)의 상태 관리: `OPEN / SOLD / EXPIRED`
    - 가격 계산에 필요한 데이터 보유: `base_price, min_price, start_at, end_at, expire_at, decay_type`
    - 가격 계산 정책 실행(가격은 저장하지 않음)
- 핵심 불변식
    - `expire_at` 이후에는 `OPEN`일 수 없다.
    - `SOLD`는 단 1번만 가능(재판매 없음).
    - 조회(quote)는 Offer 상태를 변경하지 않는다(No Hold).

### 1.2 Order Aggregate (주문 단위)
- 책임
    - 주문 상태 관리: `PENDING / PAID / CANCELLED`
    - 멱등성(Idempotency) 관리: `idempotency_key`
    - 결제 검증에 필요한 값 기록: `client_price`, `final_price(server_price)`, `quoted_at(로그)`
- 핵심 불변식
    - `PAID` 상태는 최종 상태에 준하며, 동일 주문의 중복 결제는 허용하지 않는다.
    - 동일 `(user_id, idempotency_key)` 재요청은 동일 결과를 반환해야 한다(정책은 API_CONTRACT에서 확정).

### 1.3 AccessGrant Aggregate (권리 부여 단위)
- 책임
    - 결제 성공 이후 이용 권리 발급(QR/PIN) 및 만료 관리
    - `ACTIVE / REVOKED / EXPIRED` 상태 관리
- 핵심 불변식
    - `order_id` 당 AccessGrant는 1개(유니크)
    - 유효기간(`valid_from ~ valid_to`)을 벗어나면 사용 불가

> 참고: Payment는 v1에서 “도메인 핵심”보다는 “인프라 연동” 성격이 강하므로, Order와 분리하되 도메인 결합을 최소화한다(결제 성공 여부는 Order 상태 전이로 귀결).

---

## 2) 도메인 간 참조 규칙 (Reference Rules)

### 2.1 ID 기반 간접 참조만 허용
- Order → Offer : `offerId`(또는 `offer_id`) **ID로만 참조**
- AccessGrant → Order : `orderId` **ID로만 참조**
- Offer → (다른 Aggregate 참조 금지)

### 2.2 객체 그래프(직접 참조) 금지
아래 형태를 금지한다.
- `Order`가 `Offer` 객체를 필드로 보유
- `Offer`가 `Order` 목록/참조를 보유
- JPA 연관관계 기반의 객체 그래프 확장(도메인 계층 금지)

### 2.3 예외: Aggregate 내부 소유 관계는 허용
- 동일 생명주기/완전 소유(Value Object)만 허용
- 예: `Order` 내부 `IdempotencyKey`, `Money(Price)` 같은 값 객체

---

## 3) 책임 분리 (Layer Responsibility)

### 3.1 Domain
- 비즈니스 규칙과 불변식의 원천(Source of Truth)
- 가격 정책(PriceCalculator), 상태 전이 규칙(단, 트리거는 Application)
- 도메인 예외 정의(의미 중심)

### 3.2 Application
- 유스케이스 오케스트레이션(로드/조합/트랜잭션 경계)
- Domain을 조합하여 실제 흐름을 완성
- 동시성 제어(락 획득)와 DB 최종 방어(조건 업데이트) 수행
- Port(in/out) 기반으로 외부 의존성을 추상화

### 3.3 Infrastructure
- Application이 정의한 Port(out)의 구현체 제공
- DB(JPA), Redis Lock, Clock(UTC), 결제 연동 등을 구현

### 3.4 API
- 외부 계약(HTTP) 계층
- Validation / DTO / Exception Mapping
- UseCase 호출만 수행(Repository 직접 접근 금지)

---

## 4) 가격/시간 정책에 따른 관계 영향

### 4.1 가격은 저장하지 않는다
- DB에 “현재가” 컬럼을 두지 않는다.
- 조회/결제 시점에 서버가 `f(now)`로 계산한다.

### 4.2 시간 기준
- now는 UTC 기준
- 계산 기준 시각: `truncateToSecond(now)`
- 10원 단위 내림(floor)

### 4.3 quotedAt 정책(v1)
- `quoted_at`은 저장/로그 목적(차단 로직은 v2로 이월)
- v1에서 Quote TTL 기반 차단을 도입하지 않는다(UX/예외 케이스 증가 방지)

---

## 5) 동시성/상태 전이 정책에 따른 관계 영향

### 5.1 No Hold (조회는 상태를 변경하지 않는다)
- Quote 조회는 “견적 고정”이 아니다.
- 선점(Hold) 상태 및 TTL은 제공하지 않는다.

### 5.2 결제 순간만 보호한다
- 락 단위: Offer 단위 (`lock:offer:{offerId}`)
- 락 범위: 결제 트랜잭션 구간(짧게)
- 최종 방어: DB 조건 업데이트로 `OPEN → SOLD` 단일 승자 보장

---

## 6) 최소 유스케이스와 관계 요약 (v1)

### UC-01 Quote 조회
- 입력: offerId
- 동작: Offer 로드 → price 계산 → 응답
- 상태 변경: 없음

### UC-02 Order 생성
- 입력: offerId, userId, clientPrice, (quotedAt), idempotencyKey
- 동작: 멱등성 검증 → PENDING Order 생성
- 상태 변경: Order만 생성

### UC-03 Pay Order
- 입력: orderId (또는 offerId + idempotencyKey)
- 동작: 락 획득 → Offer 상태/만료/가격 검증 → Offer SOLD → Order PAID → AccessGrant 발급(동기 또는 이벤트)
- 상태 변경: Offer, Order, AccessGrant

---

## 7) 결론(고정 규칙)
- 도메인 간 관계는 **ID 참조만** 사용한다.
- Aggregate 경계는 `Offer / Order / AccessGrant`로 고정한다.
- 가격은 상태가 아니라 함수이며, 계산은 Domain, 조합은 Application 책임이다.
- No Hold 정책으로 조회는 상태를 변경하지 않으며, 결제 순간만 동시성 제어한다.
