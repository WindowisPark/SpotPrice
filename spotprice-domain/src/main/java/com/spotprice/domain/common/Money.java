package com.spotprice.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 금액을 표현하는 Value Object
 *
 * ## 구현 고려사항
 *
 * ### 1. 불변성 (Immutability)
 * - VO는 불변이어야 함
 * - Java record 사용 권장 (Java 21)
 *
 * ### 2. 필드
 * - 금액 값 (BigDecimal 권장 - 정밀도 이슈)
 * - 통화는 v1에서 KRW 고정이면 생략 가능
 *
 * ### 3. 정책 (DOMAIN_RELATIONSHIP.md 기준)
 * - 10원 단위 내림 (floor)
 * - 음수 금액 허용 여부 결정 필요
 *
 * ### 4. 팩토리 메서드
 * - of(long), of(BigDecimal) 등
 * - 10원 단위 내림 적용 시점: 생성 시? 별도 메서드?
 *
 * ### 5. 연산 메서드 (필요시)
 * - add, subtract, multiply 등
 * - 비교: isGreaterThan, isLessThan, compareTo
 *
 * ### 6. 동등성
 * - record 쓰면 equals/hashCode 자동 생성
 * - 아니면 직접 구현 필요
 *
 * ### 7. 검증
 * - null 체크
 * - 음수 체크 (정책에 따라)
 */
public record Money(BigDecimal amount) {

    // 10원 단위 내림 처리를 위한 상수
    private static final BigDecimal UNIT = new BigDecimal("10");

    /**
     * [3. 정책 & 7. 검증] 컴팩트 생성자
     * record는 필드를 정의하지 않아도 상단 (BigDecimal amount)가 필드가 됩니다.
     */
    public Money {
        // 7. 검증: null 체크
        Objects.requireNonNull(amount, "금액은 필수입니다.");

        // 3. 정책: 10원 단위 내림 (floor)
        // 예: 1238원 -> 1230원
        amount = amount.divide(UNIT, 0, RoundingMode.FLOOR).multiply(UNIT);

        // 3. 정책: 음수 금액 허용 여부 (여기서는 비허용으로 가정)
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0보다 작을 수 없습니다.");
        }
    }

    // --- [4. 팩토리 메서드] ---

    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    // --- [5. 연산 메서드] ---

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        if (other.isGreaterThan(this)) {
            throw new IllegalArgumentException(
                    "차감 금액이 보유 금액보다 큽니다: " + this.amount + " - " + other.amount);
        }
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(BigDecimal rate){
        return new Money(this.amount.multiply(rate));
    }

    // --- [5. 비교 메서드] ---

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    // 6. 동등성(equals/hashCode)은 record가 자동으로 만들어주므로 생략 가능
}
