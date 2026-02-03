# SpotPrice ERD (v1)

> 기준 문서: [POLICY.md](./POLICY.md)
> 원본: [dbdiagram.io](https://dbdiagram.io/d/SpotPrice-69805efcbd82f5fce2521ee6)

## 설계 원칙

- **No FK Constraints**: 모든 참조는 논리적(indirect) 참조만 사용
- **Soft Delete**: `deleted_at` 컬럼으로 논리 삭제
- **Timestamp Precision**: `datetime(3)` 밀리초 정밀도

---

## Entity Relationship Diagram

```
┌──────────────┐
│    users     │
├──────────────┤
│ id (PK)      │◄─────────────────────────────┐
│ email        │                              │
│ password_hash│                              │
└──────────────┘                              │
       ▲                                      │
       │ owner_user_id                        │ user_id
       │                                      │
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│    spots     │      │   offers     │      │   orders     │
├──────────────┤      ├──────────────┤      ├──────────────┤
│ id (PK)      │◄─────│ spot_id      │◄─────│ offer_id     │
│ owner_user_id│      │ id (PK)      │      │ id (PK)      │
│ title        │      │ start_at     │      │ user_id      │
│ address      │      │ end_at       │      │ status       │
│ status       │      │ expire_at    │      │ client_price │
└──────┬───────┘      │ list_price   │      │ quoted_at    │
       │              │ base_price   │      │ final_price  │
       │              │ min_price    │      │ idempotency_ │
       ▼              │ decay_type   │      └──────┬───────┘
┌──────────────┐      │ status       │             │
│ spot_images  │      └──────────────┘             │
├──────────────┤                                   │
│ id (PK)      │                          ┌───────┴───────┐
│ spot_id      │                          │               │
│ url          │                          ▼               ▼
│ is_primary   │                   ┌──────────────┐ ┌──────────────┐
│ order_index  │                   │  payments    │ │access_grants │
└──────────────┘                   ├──────────────┤ ├──────────────┤
                                   │ id (PK)      │ │ id (PK)      │
                                   │ order_id     │ │ order_id     │
                                   │ status       │ │ grant_type   │
                                   │ provider     │ │ grant_value  │
                                   │ amount       │ │ valid_from   │
                                   └──────────────┘ │ valid_to     │
                                                    │ status       │
                                                    └──────────────┘
```

---

## Tables

### 1. users
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| email | VARCHAR(255) | UNIQUE, NOT NULL | |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

### 2. spots
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| owner_user_id | BIGINT | NOT NULL | → users.id (논리적) |
| title | VARCHAR(200) | NOT NULL | 공간명 |
| address | VARCHAR(500) | | 주소 |
| status | VARCHAR(16) | NOT NULL | ACTIVE, INACTIVE |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

### 3. spot_images
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| spot_id | BIGINT | NOT NULL | → spots.id (논리적) |
| url | VARCHAR(1000) | NOT NULL | 이미지 URL |
| is_primary | TINYINT | NOT NULL | 대표 이미지 여부 |
| order_index | INT | | 정렬 순서 |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

### 4. offers
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| spot_id | BIGINT | NOT NULL | → spots.id (논리적) |
| start_at | DATETIME(3) | NOT NULL | 이용 시작 시각 |
| end_at | DATETIME(3) | NOT NULL | 이용 종료 시각 |
| expire_at | DATETIME(3) | NOT NULL | 판매 만료 시각 |
| list_price | INT | NOT NULL | 정가 (표시용) |
| base_price | INT | NOT NULL | 시작 가격 |
| min_price | INT | NOT NULL | 최저 가격 |
| decay_type | VARCHAR(16) | NOT NULL | LINEAR, EXPONENTIAL |
| status | VARCHAR(16) | NOT NULL | OPEN, SOLD, EXPIRED |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

**Unique Index**: `(spot_id, start_at, end_at)`

**정책 반영:**
- ❌ `current_price` 컬럼 없음
- ✅ 가격은 `f(now)`로 계산

### 5. orders
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_id | BIGINT | NOT NULL | → users.id (논리적) |
| offer_id | BIGINT | NOT NULL | → offers.id (논리적) |
| status | VARCHAR(16) | NOT NULL | PENDING, PAID, CANCELLED |
| client_price | INT | NOT NULL | 클라이언트 요청 가격 |
| quoted_at | DATETIME(3) | | 견적 조회 시각 |
| final_price | INT | | 최종 결제 가격 |
| idempotency_key | VARCHAR(64) | NOT NULL | 멱등성 키 |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

**Unique Index**: `(user_id, idempotency_key)`

### 6. payments
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| order_id | BIGINT | NOT NULL | → orders.id (논리적) |
| status | VARCHAR(16) | NOT NULL | INIT, SUCCESS, FAILED |
| provider | VARCHAR(32) | | PG사 |
| provider_tx_id | VARCHAR(128) | | PG 거래 ID |
| amount | INT | NOT NULL | 결제 금액 |
| fail_code | VARCHAR(64) | | 실패 코드 |
| fail_reason | VARCHAR(500) | | 실패 사유 |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

### 7. access_grants
| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| order_id | BIGINT | NOT NULL | → orders.id (논리적) |
| grant_type | VARCHAR(16) | NOT NULL | QR, PIN |
| grant_value | VARCHAR(255) | NOT NULL | 접근 토큰 값 |
| valid_from | DATETIME(3) | NOT NULL | 유효 시작 |
| valid_to | DATETIME(3) | NOT NULL | 유효 종료 |
| status | VARCHAR(16) | NOT NULL | ACTIVE, REVOKED, EXPIRED |
| created_at | DATETIME(3) | NOT NULL | |
| updated_at | DATETIME(3) | | |
| deleted_at | DATETIME(3) | | Soft delete |

**Unique Index**: `(order_id)`

---

## Logical References (No FK)

| From | To | Description |
|------|-----|-------------|
| spots.owner_user_id | users.id | 공간 소유자 |
| spot_images.spot_id | spots.id | 공간 이미지 |
| offers.spot_id | spots.id | 공간별 Offer |
| orders.user_id | users.id | 주문자 |
| orders.offer_id | offers.id | 주문 대상 Offer |
| payments.order_id | orders.id | 주문 결제 |
| access_grants.order_id | orders.id | 접근 권한 |
