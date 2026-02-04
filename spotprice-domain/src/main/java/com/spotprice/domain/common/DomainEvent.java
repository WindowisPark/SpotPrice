package com.spotprice.domain.common;

import java.time.Instant;

/**
 * 도메인 이벤트 마커 인터페이스
 *
 * TODO: 구현 고려사항
 *
 * ### 1. 필수 필드
 * - 이벤트 발생 시간 (occurredAt)
 * - 애그리거트 ID (어떤 Offer에서 발생했는지)
 *
 * ### 2. 선택적 필드
 * - 이벤트 ID (UUID) - 멱등성 처리에 유용
 * - 이벤트 타입명 - 직렬화/역직렬화 시 유용
 *
 * ### 3. 설계 선택지
 * - 인터페이스 vs 추상 클래스
 * - record로 구현 가능한지? (불변성 보장)
 *
 * ### 4. 엣지 케이스
 * - occurredAt을 누가 설정? 이벤트 생성 시점 vs 등록 시점
 */
public interface DomainEvent {

    Instant occurredAt();

    // TODO: 필요한 메서드 추가 고려
    // - String eventType();
    // - Long aggregateId();
}
