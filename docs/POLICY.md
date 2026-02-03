# SpotPrice Policy Specification (v1)

> Time-based Reverse Auction Platform for Idle Spaces
> "시간이 가격과 점유권을 동시에 결정하는 시스템"

## 0. 설계 철학 (Design Principles)

| 원칙 | 설명 |
|------|------|
| 가격은 저장하지 않는다 | DB에 current price 없음. 모든 가격은 f(now)로 계산 |
| 선점(Hold) 없음 | 사용자 선점, 임시 예약, TTL 기반 가격 고정 없음 |
| 결제 승자는 단 1명 | Offer는 논리적 재고 1 |
| 정합성 기준은 서버 | 클라이언트 가격은 참고값, 최종 결정은 서버 |
| 정책이 코드보다 우선 | 모든 구현은 이 문서를 기준으로 검증 |

---

## 1. 도메인 모델 정책

### 1.1 Offer (판매 단위)
```
Offer = Spot + TimeSlot + ExpireAt
```
- Offer 1개당 재고는 **논리적으로 1**

### 1.2 Offer 상태
| 상태 | 설명 |
|------|------|
| OPEN | 판매 가능 |
| SOLD | 결제 완료 |
| EXPIRED | 만료 |

---

## 2. 가격 정책 (Price Policy)

### 2.1 가격 저장 정책
- ❌ current_price 컬럼 없음
- ❌ 가격 변경 이벤트 없음
- ✅ 가격은 항상 `f(now)`로 계산

**저장하는 값 (Offer 생성 시 필수):**
- `base_price`: 시작 가격
- `min_price`: 최저 가격

### 2.2 가격 하락 기준
- 기준 구간: `start_at` → `expire_at`
- 기준 시각: 서버 시간 `now`

### 2.3 가격 함수 (v1: LINEAR)
```
price(t) = base_price - (base_price - min_price) * (elapsed / total)

where:
  elapsed = now - start_at
  total = expire_at - start_at
```

> EXPONENTIAL은 enum만 정의, v2에서 도입

### 2.4 시간 정밀도 정책
- 모든 시간 계산은 **UTC 기준**
- 서버는 `Instant.now()` 사용
- 가격 계산 시점은 **초 단위로 내림(truncate)**

```java
calcTime = truncateToSecond(now)
```

**효과:**
- 가격 경계가 "매 초"로 고정
- 조회/결제 간 계산 기준 동일
- ms/ns 단위 경계 경쟁 제거

### 2.5 반올림 규칙
- 통화: **KRW**
- 반올림: **10원 단위 내림(floor)**
- 조회 API, 결제 검증, 최종 결제 금액에 동일 적용

---

## 3. 가격 정합성 정책 (Price Integrity)

### 3.1 결제 요청 시 클라이언트 전달 값
| 필드 | 설명 |
|------|------|
| clientPrice | 사용자가 조회한 가격 |
| quotedAt | 가격을 조회한 시각 (UTC) |

> v1에서 quotedAt은 저장/로그 목적. TTL 차단 로직 없음.

### 3.2 결제 승인 규칙 (핵심)
```
serverPrice <= clientPrice → 승인
serverPrice > clientPrice  → 거절 (409 Conflict)
```

| 상황 | 결과 |
|------|------|
| 가격이 더 싸진 경우 | ✅ 허용 |
| 가격이 더 비싸진 경우 | ❌ 차단 (최신 serverPrice 반환) |

**최종 결제 금액은 항상 serverPrice**

---

## 4. 동시성 제어 정책 (Concurrency Policy)

### 4.1 분산락의 목적
- ❌ 사용자 선점용 아님
- ✅ 서버 내부 상태 전이 보호용

### 4.2 락 사용 범위
| 항목 | 값 |
|------|-----|
| 락 단위 | Offer |
| 락 키 | `lock:offer:{offerId}` |
| 유지 시간 | 결제 트랜잭션 동안만 (수십~수백 ms) |

### 4.3 No Hold Policy
- ❌ 사용자 선점(Hold) 없음
- ❌ 예약/TTL 개념 없음
- ✅ 결제 요청 시점에만 락 획득
- ✅ 성공 시 즉시 SOLD
- ✅ 실패 시 즉시 경쟁 실패

> 가격 하락 유도, 그리핑, 결제 방해 행위를 설계적으로 차단

### 4.4 최종 방어선 (DB)
```sql
UPDATE offers
SET status = 'SOLD'
WHERE id = :offerId
  AND status = 'OPEN';

-- 영향 row = 1 → 성공
-- 영향 row = 0 → 이미 선점됨
```

> Redis + DB 이중 방어 구조

---

## 5. 주문/결제 정책 (Order Policy)

### 5.1 주문 상태
| 상태 | 설명 |
|------|------|
| PENDING | 결제 대기 |
| PAID | 결제 완료 |
| CANCELLED | 취소됨 |

### 5.2 결제 확정 정책
- v1에서 **PAID는 불변 상태**
- 결제 성공 후 Offer는 재오픈하지 않음

**공급자 취소 시:**
- Order: CANCELLED
- AccessGrant: REVOKED
- 환불/정산은 후속 범위

### 5.3 멱등성 (Idempotency)
- `idempotency_key` 필수
- `(user_id, idempotency_key)` 유니크
- 동일 키 재요청 시 같은 결과 반환

---

## 6. 접근 권한 부여 정책 (Access Grant)

- 결제 성공 후 AccessGrant 발급
- 토큰 타입: QR / PIN
- 결제 트랜잭션과 분리된 비동기 이벤트 처리

```
Payment Success
 → Event Publish
   → AccessGrant Generate
   → Notification Send
```

---

## 7. 만료 정책 (Expire Policy)

- `now > expire_at`인 Offer는 만료
- 목록 조회에서 기본 제외
- 단건 조회 시: **410 Gone** 반환

---

## 8. API 에러 정책

| 상황 | 상태 코드 |
|------|----------|
| 만료 (EXPIRED) | 410 Gone |
| 이미 판매됨 / 경쟁 패배 | 409 Conflict |
| 가격 상승 | 409 Conflict (+ serverPrice 반환) |
| Offer 없음 | 404 Not Found |

---

## 9. 보안 정책

- 인증: Spring Security
- 토큰 전달: HttpOnly + Secure 쿠키
- 중복 결제 방지: idempotency_key

---

## 10. 감사 로그 (Audit Log)

| 이벤트 | 설명 |
|--------|------|
| OFFER_VIEW | 가격 계산 결과 포함 |
| PAY_ATTEMPT | 결제 시도 |
| PAY_SUCCESS | 결제 성공 |
| PAY_FAIL | 결제 실패 |
| OFFER_SOLD | Offer 판매 완료 |

---

## 11. MVP 제외 범위

- ❌ Hold / 예약 선점
- ❌ 가격 고정 견적 (Quote TTL)
- ❌ EXPONENTIAL 가격 함수 (v2)
- ❌ 외부 PG 연동
- ❌ 복잡한 환불/정산

---

## 12. 문서 역할

이 문서는 SpotPrice v1의 **최종 정책 명세**이며, 다음 문서는 이 문서를 기준으로만 작성한다:

- ERD
- DDL
- API Contract
- 테스트 시나리오
- README
