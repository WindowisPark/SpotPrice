# SpotPrice Roadmap

> 기준 문서: [POLICY.md](./POLICY.md), [API_CONTRACT.md](./API_CONTRACT.md)

---

## v1 현황

### 구현 완료 (Core Flow)

```
Offer 조회(Quote) → 주문 생성(CreateOrder) → 결제(PayOrder) → 접근 권한 발급(IssueAccessGrant)
```

| 유스케이스 | 정책 섹션 | 커밋 |
|-----------|----------|------|
| Quote | §2 가격 정책 | `b2c6ea2` |
| CreateOrder | §4 동시성 + §5 주문 | `d08108d` |
| PayOrder | §3 가격 정합성 + §5 결제 | `ba877a9` |
| IssueAccessGrant | §6 접근 권한 | `8ec089a` |

### v1 잔여 작업

#### 1. Offer 만료 처리 (§7)
- [ ] 목록 조회 시 `now > expire_at` Offer 제외
- [ ] 단건 조회 시 만료된 Offer → 410 Gone 응답

#### 2. API 에러 정책 (§8)
- [ ] 공통 에러 응답 구조 (`success`/`error` wrapper)
- [ ] 도메인 예외 → HTTP 상태 코드 매핑 (`@RestControllerAdvice`)
  - 410 Gone: OFFER_EXPIRED
  - 409 Conflict: OFFER_ALREADY_SOLD, PRICE_INCREASED, PAYMENT_CONFLICT
  - 404 Not Found: OFFER_NOT_FOUND, ORDER_NOT_FOUND

#### 3. 보안 (§9)
- [ ] Spring Security 기본 설정
- [ ] JWT 발급/검증 (HttpOnly + Secure Cookie)
- [ ] 인증 필요 API에 `@Auth` 적용

#### 4. 감사 로그 (§10)
- [ ] 이벤트 로깅: OFFER_VIEW, PAY_ATTEMPT, PAY_SUCCESS, PAY_FAIL, OFFER_SOLD

---

## v1.5 — Thymeleaf Web UI

백엔드 기능을 브라우저에서 직접 확인/테스트할 수 있는 서버 렌더링 UI.
**목적**: API 동작 검증, 전체 플로우 시각적 확인, v2 프론트엔드 분리 전 프로토타입.

### 구성

```
spotprice-api/
└─ src/main/
   ├─ java/.../web/          # Thymeleaf Controller (API Controller와 분리)
   └─ resources/
      ├─ templates/           # .html
      └─ static/              # CSS, JS
```

### 페이지 목록

| 페이지 | URL | 설명 |
|--------|-----|------|
| Offer 목록 | `/` | OPEN 상태 Offer 목록, 실시간 가격 표시 |
| Offer 상세 | `/offers/{id}` | 가격 변동 시각화, 주문 버튼 |
| 주문 확인 | `/orders/{id}` | 주문 정보 + 결제 버튼 |
| 결제 결과 | `/orders/{id}/result` | 성공 시 AccessGrant(PIN/QR) 표시 |
| 내 주문 | `/my/orders` | 주문 이력 |

### 기술 선택

- **Thymeleaf**: Spring Boot 기본 내장, 추가 빌드 도구 불필요
- **htmx (선택)**: 페이지 전환 없이 가격 갱신 등 부분 업데이트
- **CSS**: 최소한의 스타일 (classless CSS 또는 Pico CSS)

### 고려사항

- Thymeleaf Controller는 기존 UseCase를 직접 호출 (API Controller와 동일 서비스 레이어 사용)
- 인증이 구현되기 전까지는 테스트용 고정 userId 사용
- v2에서 프론트엔드 분리 시 Thymeleaf는 제거하고 API만 남김

---

## v2 — 확장

### User 도메인
- [ ] User Aggregate (회원가입, 프로필)
- [ ] 역할 분리: 공급자(Host) / 구매자(Guest)
- [ ] 공급자: Offer 생성/관리 화면
- [ ] 구매자: 주문/AccessGrant 조회

### Offer 관리
- [ ] 공급자용 Offer 생성 API/UI
- [ ] Offer 목록 필터링 (지역, 시간대, 가격 범위)
- [ ] EXPONENTIAL 가격 함수 (§2.3)

### 결제 고도화
- [ ] 외부 PG 연동 (토스페이먼츠 등)
- [ ] 환불/정산 프로세스

### 운영
- [ ] 프론트엔드 분리 (React/Next.js)
- [ ] Notification (이메일/푸시)
- [ ] 모니터링/알림

---

## 우선순위 제안

```
v1 잔여 (에러 처리 → 만료 → 보안)
  → v1.5 Thymeleaf UI
    → v2 User + Offer 관리
      → v2 PG 연동
```
