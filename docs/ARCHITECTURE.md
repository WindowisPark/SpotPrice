# SpotPrice Architecture

## Overview

SpotPrice는 시간 기반 동적 가격(Spot Pricing)을 제공하는 시스템입니다.
헥사고날 아키텍처(Ports & Adapters)를 기반으로 설계되었습니다.

## Module Structure

```
spotprice/
├─ spotprice-domain        # 도메인 모델 (순수 Java)
├─ spotprice-application   # 유즈케이스, 포트 정의
├─ spotprice-infrastructure# 어댑터 구현 (DB, 외부 서비스)
└─ spotprice-api           # REST API (Spring Boot)
```

## Layer Dependencies

```
[API] → [Application] → [Domain]
         ↓
    [Infrastructure]
```

- **Domain**: 외부 의존성 없음. 순수 비즈니스 로직
- **Application**: Domain 의존. 포트 인터페이스 정의
- **Infrastructure**: Application 포트 구현. Spring/JPA 의존
- **API**: Application 유즈케이스 호출. Spring Web 의존

## Key Patterns

### Hexagonal Architecture
- **Inbound Ports**: `application/port/in/*UseCase.java`
- **Outbound Ports**: `application/port/out/*Port.java`
- **Adapters**: `infrastructure/` 및 `api/`

### Domain Model
- **Aggregate Root**: `Offer`, `Order`
- **Value Object**: `IdempotencyKey`
- **Domain Exception**: 비즈니스 규칙 위반 시 발생

## Core Concepts

### Offer
- 판매자가 생성하는 가격 제안
- 시간에 따라 가격이 변동 (DecayType)
- 만료 시간 존재

### Order
- 구매자가 Offer를 구매하는 행위
- 멱등성 키로 중복 주문 방지
- 주문 시점의 가격을 Lock

### Price Calculation
- NONE: 고정 가격
- LINEAR: 선형 감소
- EXPONENTIAL: 지수 감소
