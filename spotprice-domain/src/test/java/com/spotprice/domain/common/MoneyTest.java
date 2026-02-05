package com.spotprice.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("10원 단위로 내림 처리된다")
        void floorToTenWon() {
            Money money = Money.of(1238);

            assertThat(money.amount()).isEqualByComparingTo("1230");
        }

        @Test
        @DisplayName("이미 10원 단위인 금액은 그대로 유지된다")
        void alreadyRounded() {
            Money money = Money.of(1230);

            assertThat(money.amount()).isEqualByComparingTo("1230");
        }

        @Test
        @DisplayName("null 금액은 허용되지 않는다")
        void nullNotAllowed() {
            assertThatThrownBy(() -> Money.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("음수 금액은 허용되지 않는다")
        void negativeNotAllowed() {
            assertThatThrownBy(() -> Money.of(-100))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 작을 수 없습니다");
        }

        @Test
        @DisplayName("0원은 허용된다")
        void zeroAllowed() {
            Money money = Money.of(0);

            assertThat(money.amount()).isEqualByComparingTo("0");
        }
    }

    @Nested
    @DisplayName("연산")
    class Operations {

        @Test
        @DisplayName("두 금액을 더한다")
        void add() {
            Money a = Money.of(1000);
            Money b = Money.of(500);

            Money result = a.add(b);

            assertThat(result.amount()).isEqualByComparingTo("1500");
        }

        @Test
        @DisplayName("두 금액을 뺀다")
        void subtract() {
            Money a = Money.of(1000);
            Money b = Money.of(300);

            Money result = a.subtract(b);

            assertThat(result.amount()).isEqualByComparingTo("700");
        }

        @Test
        @DisplayName("뺄셈 결과가 음수면 예외 발생")
        void subtractResultNegative() {
            Money a = Money.of(100);
            Money b = Money.of(500);

            assertThatThrownBy(() -> a.subtract(b))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("차감 금액이 보유 금액보다 큽니다");
        }

        @Test
        @DisplayName("비율을 곱한다")
        void multiply() {
            Money money = Money.of(1000);

            Money result = money.multiply(new BigDecimal("0.5"));

            assertThat(result.amount()).isEqualByComparingTo("500");
        }

        @Test
        @DisplayName("곱셈 결과도 10원 단위로 내림된다")
        void multiplyFloors() {
            Money money = Money.of(1000);

            Money result = money.multiply(new BigDecimal("0.33"));

            assertThat(result.amount()).isEqualByComparingTo("330");
        }
    }

    @Nested
    @DisplayName("비교")
    class Comparison {

        @Test
        @DisplayName("더 큰 금액인지 비교한다")
        void isGreaterThan() {
            Money a = Money.of(1000);
            Money b = Money.of(500);

            assertThat(a.isGreaterThan(b)).isTrue();
            assertThat(b.isGreaterThan(a)).isFalse();
        }

        @Test
        @DisplayName("같은 금액은 greater가 아니다")
        void equalIsNotGreater() {
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            assertThat(a.isGreaterThan(b)).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성")
    class Equality {

        @Test
        @DisplayName("같은 금액은 동등하다")
        void equals() {
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("10원 내림 후 같은 금액은 동등하다")
        void equalsAfterFloor() {
            Money a = Money.of(1005);
            Money b = Money.of(1009);

            assertThat(a).isEqualTo(b);
        }
    }
}
