# SpotPrice DDL (v1)

> 기준 문서: [ERD.md](./ERD.md), [POLICY.md](./POLICY.md)
> Target DB: MySQL 8.0+ (PostgreSQL 호환 주석 포함)

## 설계 원칙

- **No FK Constraints**: 애플리케이션 레벨에서 참조 무결성 관리
- **Soft Delete**: `deleted_at IS NULL` 조건으로 조회
- **UTC Timestamp**: 모든 시간은 UTC 기준 저장
- **DATETIME(3)**: 밀리초 정밀도

---

## DDL

```sql
-- ============================================
-- SpotPrice v1 DDL (MySQL 8.0+)
-- ============================================

-- 1. users
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. spots
CREATE TABLE spots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_user_id BIGINT NOT NULL,

    title VARCHAR(200) NOT NULL,
    address VARCHAR(500) NULL,

    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    INDEX idx_spots_owner (owner_user_id),
    INDEX idx_spots_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. spot_images
CREATE TABLE spot_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    spot_id BIGINT NOT NULL,

    url VARCHAR(1000) NOT NULL,
    is_primary TINYINT NOT NULL DEFAULT 0,
    order_index INT NULL,

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    INDEX idx_spot_images_spot (spot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. offers
CREATE TABLE offers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    spot_id BIGINT NOT NULL,

    start_at DATETIME(3) NOT NULL,
    end_at DATETIME(3) NOT NULL,
    expire_at DATETIME(3) NOT NULL,

    list_price INT NOT NULL,
    base_price INT NOT NULL,
    min_price INT NOT NULL,

    decay_type VARCHAR(16) NOT NULL DEFAULT 'LINEAR',
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN',

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_offers_spot_time (spot_id, start_at, end_at),
    INDEX idx_offers_status_expire (status, expire_at),
    INDEX idx_offers_spot_status (spot_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. orders
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,

    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',

    client_price INT NOT NULL,
    quoted_at DATETIME(3) NULL,
    final_price INT NULL,

    idempotency_key VARCHAR(64) NOT NULL,

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_orders_user_idempotency (user_id, idempotency_key),
    INDEX idx_orders_offer (offer_id),
    INDEX idx_orders_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. payments
CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,

    status VARCHAR(16) NOT NULL DEFAULT 'INIT',
    provider VARCHAR(32) NULL,
    provider_tx_id VARCHAR(128) NULL,

    amount INT NOT NULL,
    fail_code VARCHAR(64) NULL,
    fail_reason VARCHAR(500) NULL,

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    INDEX idx_payments_order (order_id),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. access_grants
CREATE TABLE access_grants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,

    grant_type VARCHAR(16) NOT NULL,
    grant_value VARCHAR(255) NOT NULL,

    valid_from DATETIME(3) NOT NULL,
    valid_to DATETIME(3) NOT NULL,

    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NULL ON UPDATE CURRENT_TIMESTAMP(3),
    deleted_at DATETIME(3) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_access_grants_order (order_id),
    INDEX idx_access_grants_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## Enum Values

### spots.status
| Value | Description |
|-------|-------------|
| ACTIVE | 활성 공간 |
| INACTIVE | 비활성 공간 |

### offers.decay_type
| Value | Description |
|-------|-------------|
| LINEAR | 선형 가격 하락 (v1) |
| EXPONENTIAL | 지수 가격 하락 (v2 예정) |

### offers.status
| Value | Description |
|-------|-------------|
| OPEN | 판매 가능 |
| SOLD | 판매 완료 |
| EXPIRED | 만료됨 |

### orders.status
| Value | Description |
|-------|-------------|
| PENDING | 결제 대기 |
| PAID | 결제 완료 |
| CANCELLED | 취소됨 |

### payments.status
| Value | Description |
|-------|-------------|
| INIT | 초기화 |
| SUCCESS | 성공 |
| FAILED | 실패 |

### access_grants.grant_type
| Value | Description |
|-------|-------------|
| QR | QR 코드 |
| PIN | PIN 번호 |

### access_grants.status
| Value | Description |
|-------|-------------|
| ACTIVE | 활성 |
| REVOKED | 취소됨 |
| EXPIRED | 만료됨 |

---

## Key Indexes

| Table | Index | Purpose |
|-------|-------|---------|
| offers | `uk_offers_spot_time` | 동일 시간대 중복 Offer 방지 |
| offers | `idx_offers_status_expire` | OPEN 상태 만료 체크 |
| orders | `uk_orders_user_idempotency` | 멱등성 보장 |
| access_grants | `uk_access_grants_order` | Order당 1개 보장 |

---

## Flyway Migration

```
V1__create_users.sql
V2__create_spots.sql
V3__create_spot_images.sql
V4__create_offers.sql
V5__create_orders.sql
V6__create_payments.sql
V7__create_access_grants.sql
```

---

## PostgreSQL 호환 참고

```sql
-- MySQL: DATETIME(3)
-- PostgreSQL: TIMESTAMP(3)

-- MySQL: AUTO_INCREMENT
-- PostgreSQL: GENERATED ALWAYS AS IDENTITY

-- MySQL: TINYINT
-- PostgreSQL: SMALLINT
```
