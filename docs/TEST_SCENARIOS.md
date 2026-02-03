# SpotPrice Test Scenarios (v1)

> 기준 문서: [POLICY.md](./POLICY.md)

---

## 1. 가격 계산 (Price Calculation)

### TC-PRICE-001: LINEAR 가격 계산 - 시작 시점
```
Given:
  - base_price = 50,000
  - min_price = 20,000
  - start_at = 2024-01-15T18:00:00Z
  - expire_at = 2024-01-15T20:00:00Z (2시간)
  - now = 2024-01-15T18:00:00Z (시작 시점)

When: 가격 계산

Then: price = 50,000 (base_price)
```

### TC-PRICE-002: LINEAR 가격 계산 - 중간 시점 (50%)
```
Given:
  - base_price = 50,000
  - min_price = 20,000
  - start_at = 2024-01-15T18:00:00Z
  - expire_at = 2024-01-15T20:00:00Z
  - now = 2024-01-15T19:00:00Z (50% 경과)

When: 가격 계산

Then:
  - elapsed = 3600초, total = 7200초
  - price = 50,000 - (50,000 - 20,000) * 0.5 = 35,000
```

### TC-PRICE-003: LINEAR 가격 계산 - 만료 직전
```
Given:
  - base_price = 50,000
  - min_price = 20,000
  - now = 2024-01-15T19:59:59Z (거의 만료)

When: 가격 계산

Then: price ≈ 20,000 (min_price 근접)
```

### TC-PRICE-004: 10원 단위 내림 반올림
```
Given:
  - 계산된 가격 = 35,456원

When: 반올림 적용

Then: price = 35,450원
```

### TC-PRICE-005: 초 단위 truncate
```
Given:
  - now = 2024-01-15T19:00:00.999Z

When: calcTime 계산

Then: calcTime = 2024-01-15T19:00:00Z (밀리초 제거)
```

---

## 2. Offer 상태 (Offer Status)

### TC-OFFER-001: OPEN 상태 조회
```
Given: Offer (status=OPEN, expire_at > now)

When: GET /offers/{id}

Then: 200 OK, status=OPEN, currentPrice 계산됨
```

### TC-OFFER-002: EXPIRED 상태 조회
```
Given: Offer (status=OPEN, expire_at < now)

When: GET /offers/{id}

Then: 410 Gone, code=OFFER_EXPIRED
```

### TC-OFFER-003: SOLD 상태 조회
```
Given: Offer (status=SOLD)

When: GET /offers/{id}

Then: 200 OK, status=SOLD (가격 정보 포함)
```

### TC-OFFER-004: 목록에서 만료 Offer 제외
```
Given:
  - Offer A (OPEN, not expired)
  - Offer B (OPEN, expired)
  - Offer C (SOLD)

When: GET /offers

Then: Offer A만 반환
```

---

## 3. 가격 정합성 (Price Integrity)

### TC-INTEGRITY-001: 가격 하락 시 승인
```
Given:
  - Offer currentPrice = 34,000 (at payment time)
  - clientPrice = 35,000 (조회 시점)

When: POST /orders

Then:
  - 201 Created
  - finalPrice = 34,000 (serverPrice)
```

### TC-INTEGRITY-002: 가격 상승 시 거절
```
Given:
  - Offer currentPrice = 36,000 (at payment time)
  - clientPrice = 35,000 (조회 시점)

When: POST /orders

Then:
  - 409 Conflict
  - code = PRICE_INCREASED
  - serverPrice = 36,000
```

### TC-INTEGRITY-003: 가격 동일 시 승인
```
Given:
  - serverPrice = 35,000
  - clientPrice = 35,000

When: POST /orders

Then: 201 Created
```

---

## 4. 동시성 제어 (Concurrency)

### TC-CONCURRENCY-001: 동시 결제 시 1명만 성공
```
Given:
  - Offer (status=OPEN)
  - User A, User B 동시 결제 요청

When: POST /orders/{id}/pay (동시 실행)

Then:
  - 1명: 200 OK, status=PAID
  - 1명: 409 Conflict, code=PAYMENT_CONFLICT
  - Offer.status = SOLD
```

### TC-CONCURRENCY-002: DB 조건 업데이트 검증
```
Given: Offer (status=SOLD)

When:
  UPDATE offers SET status='SOLD'
  WHERE id=1 AND status='OPEN'

Then: affected rows = 0
```

### TC-CONCURRENCY-003: 락 획득 실패 시 재시도 없음
```
Given: 다른 트랜잭션이 락 보유 중

When: POST /orders/{id}/pay

Then: 409 Conflict (즉시 실패, 대기 없음)
```

---

## 5. 멱등성 (Idempotency)

### TC-IDEMPOTENCY-001: 동일 키 재요청 시 같은 결과
```
Given:
  - idempotency_key = "user1-offer1-12345"
  - 첫 요청: 201 Created, orderId=100

When: 동일 키로 재요청

Then:
  - 200 OK (또는 201)
  - orderId = 100 (동일)
  - 새 Order 생성 안됨
```

### TC-IDEMPOTENCY-002: 다른 사용자 동일 키 허용
```
Given:
  - User A: idempotency_key = "key123" → orderId=100
  - User B: idempotency_key = "key123"

When: User B POST /orders

Then: 201 Created, orderId=101 (새 주문)
```

---

## 6. 주문 상태 전이 (Order State)

### TC-ORDER-001: PENDING → PAID
```
Given: Order (status=PENDING)

When: POST /orders/{id}/pay (성공)

Then:
  - Order.status = PAID
  - Order.paidAt 설정
  - Offer.status = SOLD
  - AccessGrant 생성
```

### TC-ORDER-002: PAID 상태 불변
```
Given: Order (status=PAID)

When: 취소 시도

Then: 400 Bad Request (v1에서는 불가)
```

### TC-ORDER-003: PENDING에서 Offer 만료
```
Given:
  - Order (status=PENDING)
  - Offer가 만료됨 (now > expire_at)

When: POST /orders/{id}/pay

Then: 410 Gone, code=OFFER_EXPIRED
```

---

## 7. AccessGrant (접근 권한)

### TC-ACCESS-001: 결제 성공 시 AccessGrant 생성
```
Given: Order 결제 성공

Then:
  - AccessGrant 생성
  - grant_type = QR 또는 PIN
  - valid_from = Offer.start_at
  - valid_to = Offer.end_at
  - status = ACTIVE
```

### TC-ACCESS-002: 공급자 취소 시 REVOKED
```
Given: AccessGrant (status=ACTIVE)

When: 공급자 취소

Then:
  - AccessGrant.status = REVOKED
  - Order.status = CANCELLED
```

---

## 8. 에러 케이스 (Error Cases)

### TC-ERROR-001: 존재하지 않는 Offer
```
When: GET /offers/999999

Then: 404 Not Found, code=OFFER_NOT_FOUND
```

### TC-ERROR-002: 인증 없이 주문 생성
```
When: POST /orders (without auth)

Then: 401 Unauthorized
```

### TC-ERROR-003: 다른 사용자 주문 조회
```
Given: Order owned by User A

When: User B GET /orders/{id}

Then: 403 Forbidden
```

---

## 9. 통합 시나리오 (E2E)

### TC-E2E-001: 정상 구매 플로우
```
1. GET /offers/{id}
   → 200 OK, currentPrice=35,000, quotedAt 기록

2. POST /orders
   {offerId, clientPrice=35000, quotedAt, idempotencyKey}
   → 201 Created, orderId=100, status=PENDING

3. POST /orders/100/pay
   → 200 OK, status=PAID, accessGrant 포함

4. GET /orders/100
   → 200 OK, 전체 정보 확인
```

### TC-E2E-002: 가격 변동 구매 플로우
```
1. GET /offers/{id}
   → currentPrice=35,000, quotedAt=T1

2. (시간 경과 - 가격 하락)

3. POST /orders
   {clientPrice=35000}
   → 201 Created, finalPrice=34,000 (더 저렴)

4. 결제 성공
```

### TC-E2E-003: 경쟁 패배 플로우
```
1. User A, B 동시에 GET /offers/{id}

2. User A POST /orders → 201 Created
   User B POST /orders → 201 Created

3. User A POST /orders/{a}/pay → 200 OK (승리)
   User B POST /orders/{b}/pay → 409 Conflict (패배)
```
