# SpotPrice API Contract (v1)

> 기준 문서: [POLICY.md](./POLICY.md)
> Base URL: `/api/v1`

---

## 인증

- **방식**: HttpOnly + Secure Cookie (JWT)
- **헤더**: Cookie 자동 전송
- **인증 필요 API**: `[Auth]` 표시

---

## 공통 응답 형식

### Success
```json
{
  "success": true,
  "data": { ... }
}
```

### Error
```json
{
  "success": false,
  "error": {
    "code": "OFFER_EXPIRED",
    "message": "Offer has expired"
  }
}
```

---

## 1. Offer API

### 1.1 Offer 목록 조회
```
GET /offers
```

**Query Parameters**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| spotId | Long | N | 특정 공간 필터 |
| page | Int | N | 페이지 (default: 0) |
| size | Int | N | 페이지 크기 (default: 20) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "offerId": 1,
        "spotId": 10,
        "spotTitle": "파티룸 A",
        "startAt": "2024-01-15T20:00:00Z",
        "endAt": "2024-01-15T22:00:00Z",
        "expireAt": "2024-01-15T19:55:00Z",
        "listPrice": 50000,
        "currentPrice": 35000,
        "minPrice": 20000,
        "status": "OPEN"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100
  }
}
```

> `currentPrice`는 서버가 `f(now)`로 계산한 값

---

### 1.2 Offer 단건 조회 (Quote)
```
GET /offers/{offerId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "offerId": 1,
    "spotId": 10,
    "spotTitle": "파티룸 A",
    "spotAddress": "서울시 강남구...",
    "startAt": "2024-01-15T20:00:00Z",
    "endAt": "2024-01-15T22:00:00Z",
    "expireAt": "2024-01-15T19:55:00Z",
    "listPrice": 50000,
    "currentPrice": 35000,
    "minPrice": 20000,
    "quotedAt": "2024-01-15T18:30:00Z",
    "status": "OPEN"
  }
}
```

**Response 404** - Offer 없음
```json
{
  "success": false,
  "error": {
    "code": "OFFER_NOT_FOUND",
    "message": "Offer not found"
  }
}
```

**Response 410** - 만료됨
```json
{
  "success": false,
  "error": {
    "code": "OFFER_EXPIRED",
    "message": "Offer has expired"
  }
}
```

---

## 2. Order API

### 2.1 주문 생성 (결제 요청) `[Auth]`
```
POST /orders
```

**Request Body**
```json
{
  "offerId": 1,
  "clientPrice": 35000,
  "quotedAt": "2024-01-15T18:30:00Z",
  "idempotencyKey": "user123-offer1-1705340000"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| offerId | Long | Y | 대상 Offer ID |
| clientPrice | Int | Y | 클라이언트가 조회한 가격 |
| quotedAt | DateTime | Y | 가격 조회 시각 (UTC) |
| idempotencyKey | String | Y | 멱등성 키 |

**Response 201** - 주문 생성 성공
```json
{
  "success": true,
  "data": {
    "orderId": 100,
    "offerId": 1,
    "status": "PENDING",
    "finalPrice": 34500,
    "createdAt": "2024-01-15T18:35:00Z"
  }
}
```

> `finalPrice`는 `serverPrice` (실제 결제 금액)

**Response 409** - 가격 상승
```json
{
  "success": false,
  "error": {
    "code": "PRICE_INCREASED",
    "message": "Price has increased since quote",
    "serverPrice": 36000
  }
}
```

**Response 409** - 이미 판매됨
```json
{
  "success": false,
  "error": {
    "code": "OFFER_ALREADY_SOLD",
    "message": "Offer has already been sold"
  }
}
```

**Response 410** - 만료됨
```json
{
  "success": false,
  "error": {
    "code": "OFFER_EXPIRED",
    "message": "Offer has expired"
  }
}
```

---

### 2.2 주문 결제 확정 `[Auth]`
```
POST /orders/{orderId}/pay
```

**Response 200** - 결제 성공
```json
{
  "success": true,
  "data": {
    "orderId": 100,
    "status": "PAID",
    "finalPrice": 34500,
    "paidAt": "2024-01-15T18:35:30Z",
    "accessGrant": {
      "grantType": "QR",
      "grantValue": "https://qr.spotprice.com/abc123",
      "validFrom": "2024-01-15T20:00:00Z",
      "validTo": "2024-01-15T22:00:00Z"
    }
  }
}
```

**Response 409** - 경쟁 패배 (동시성)
```json
{
  "success": false,
  "error": {
    "code": "PAYMENT_CONFLICT",
    "message": "Another user completed the payment"
  }
}
```

---

### 2.3 내 주문 목록 조회 `[Auth]`
```
GET /orders
```

**Query Parameters**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| status | String | N | PENDING, PAID, CANCELLED |
| page | Int | N | 페이지 (default: 0) |
| size | Int | N | 페이지 크기 (default: 20) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "orderId": 100,
        "offerId": 1,
        "spotTitle": "파티룸 A",
        "status": "PAID",
        "finalPrice": 34500,
        "createdAt": "2024-01-15T18:35:00Z",
        "paidAt": "2024-01-15T18:35:30Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 5
  }
}
```

---

### 2.4 주문 상세 조회 `[Auth]`
```
GET /orders/{orderId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "orderId": 100,
    "offerId": 1,
    "spotTitle": "파티룸 A",
    "spotAddress": "서울시 강남구...",
    "startAt": "2024-01-15T20:00:00Z",
    "endAt": "2024-01-15T22:00:00Z",
    "status": "PAID",
    "clientPrice": 35000,
    "finalPrice": 34500,
    "quotedAt": "2024-01-15T18:30:00Z",
    "createdAt": "2024-01-15T18:35:00Z",
    "paidAt": "2024-01-15T18:35:30Z",
    "accessGrant": {
      "grantType": "QR",
      "grantValue": "https://qr.spotprice.com/abc123",
      "validFrom": "2024-01-15T20:00:00Z",
      "validTo": "2024-01-15T22:00:00Z",
      "status": "ACTIVE"
    }
  }
}
```

---

## 3. Auth API

### 3.1 로그인
```
POST /auth/login
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com"
  }
}
```

> JWT는 `Set-Cookie` 헤더로 전달 (HttpOnly, Secure)

---

### 3.2 로그아웃 `[Auth]`
```
POST /auth/logout
```

**Response 200**
```json
{
  "success": true
}
```

---

## 4. Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| OFFER_NOT_FOUND | 404 | Offer를 찾을 수 없음 |
| OFFER_EXPIRED | 410 | Offer 만료됨 |
| OFFER_ALREADY_SOLD | 409 | 이미 판매됨 |
| PRICE_INCREASED | 409 | 가격 상승으로 거절 |
| PAYMENT_CONFLICT | 409 | 동시성 경쟁 패배 |
| ORDER_NOT_FOUND | 404 | 주문을 찾을 수 없음 |
| INVALID_ORDER_STATUS | 400 | 잘못된 주문 상태 |
| UNAUTHORIZED | 401 | 인증 필요 |
| FORBIDDEN | 403 | 권한 없음 |
| IDEMPOTENCY_CONFLICT | 409 | 멱등성 키 중복 (기존 결과 반환) |

---

## 5. 결제 흐름 요약

```
1. GET /offers/{id}       → currentPrice, quotedAt 획득
2. POST /orders           → 주문 생성 (PENDING)
   - serverPrice <= clientPrice → 성공
   - serverPrice > clientPrice  → 409 + serverPrice
3. POST /orders/{id}/pay  → 결제 확정 (PAID)
   - 락 획득 → 상태 검증 → SOLD 전환 → AccessGrant 발급
```
