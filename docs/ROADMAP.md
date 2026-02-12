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

### 구현 완료 (v1 잔여)

| 항목 | 정책 섹션 | 커밋 | 내용 |
|------|----------|------|------|
| API 에러 정책 | §8 | `9204273` | ApiResponse 공통 응답, ErrorCode enum, 도메인 예외 체계화 |
| Offer 만료 처리 | §7 | `7bb900f` | 목록 조회 DB 필터링, PageQuery/PageResult 페이지네이션 |
| 보안 | §9 | `927306d` | User 도메인, JWT Cookie 인증, Order userId/조회, 401/403 분리 |

### 구현 완료 (v1 마무리)

| 항목 | 정책 섹션 | 내용 |
|------|----------|------|
| 감사 로그 | §10 | AuditLogPort, AuditLogAdapter(REQUIRES_NEW), 5종 이벤트 DB 기록 |

> **v1 전체 구현 완료**

---

## v1.5 — Thymeleaf Web UI ✅ 완료

Pico CSS + htmx 기반 서버 렌더링 UI. 커밋 `c43d01a`.

| 페이지 | URL | 설명 |
|--------|-----|------|
| Offer 목록 | `/` | OPEN 상태 카드 목록, 현재가 표시 |
| Offer 상세 | `/offers/{id}` | htmx 3초 가격 자동 갱신, 주문 버튼 |
| 주문 확인 | `/orders/{id}` | 주문 정보 + 결제 버튼 |
| 결제 결과 | `/orders/{id}/result` | AccessGrant PIN 표시 |
| 내 주문 | `/my/orders` | 주문 이력 테이블 |
| 로그인/회원가입 | `/login`, `/register` | 폼 기반 인증 |

**기술**: Thymeleaf + Pico CSS (CDN) + htmx (CDN)
**보안**: SecurityConfig 스마트 EntryPoint (API→JSON 401, Web→/login 리다이렉트)

---

## v2 — 확장

### User 도메인 확장
- [x] User Aggregate 기본 (v1 §9에서 구현: email, passwordHash)
- [ ] 프로필 관리 (닉네임, 연락처 등)
- [ ] 역할 분리: 공급자(Host) / 구매자(Guest)
- [ ] 공급자: Offer 생성/관리 화면
- [ ] 구매자: 주문/AccessGrant 조회
- [ ] AuthPrincipal 도입 (v1의 Long userId → AuthPrincipal(userId, email, roles))
- [ ] JWT 서버 무효화 (블랙리스트/토큰 회전)

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
v1 ✅ 완료
  → v1.5 Thymeleaf UI ✅ 완료
    → v2 User 확장 + Offer 관리
      → v2 PG 연동
```
