package com.spotprice.domain.offer;

import com.spotprice.domain.common.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceCalculatorTest {

    private PriceCalculator calculator;

    // 테스트용 기준 시간
    private static final Instant START = Instant.parse("2025-01-01T12:00:00Z");
    private static final Instant END = START.plusSeconds(600);     // 10분 후
    private static final Instant EXPIRE = START.plusSeconds(900);  // 15분 후

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    @Nested
    @DisplayName("NONE 타입")
    class NoneType {

        @Test
        @DisplayName("시간과 무관하게 기본가를 반환한다")
        void alwaysBasePrice() {
            Offer offer = createOffer(DecayType.NONE);

            assertThat(calculator.calculate(offer, START)).isEqualTo(Money.of(10000));
            assertThat(calculator.calculate(offer, END)).isEqualTo(Money.of(10000));
            assertThat(calculator.calculate(offer, EXPIRE)).isEqualTo(Money.of(10000));
        }
    }

    @Nested
    @DisplayName("LINEAR 타입")
    class LinearType {

        @Test
        @DisplayName("시작 시간에는 기본가를 반환한다")
        void atStart() {
            Offer offer = createOffer(DecayType.LINEAR);

            Money price = calculator.calculate(offer, START);

            assertThat(price).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("종료 시간에는 최저가를 반환한다")
        void atEnd() {
            Offer offer = createOffer(DecayType.LINEAR);

            Money price = calculator.calculate(offer, END);

            assertThat(price).isEqualTo(Money.of(5000));
        }

        @Test
        @DisplayName("중간 시점에는 선형 보간된 가격을 반환한다")
        void atMiddle() {
            Offer offer = createOffer(DecayType.LINEAR);
            Instant middle = START.plusSeconds(300); // 5분 후 (50%)

            Money price = calculator.calculate(offer, middle);

            // basePrice=10000, minPrice=5000, progress=0.5
            // price = 10000 - 5000 * 0.5 = 7500
            assertThat(price).isEqualTo(Money.of(7500));
        }

        @Test
        @DisplayName("시작 전에는 기본가를 반환한다")
        void beforeStart() {
            Offer offer = createOffer(DecayType.LINEAR);
            Instant before = START.minusSeconds(60);

            Money price = calculator.calculate(offer, before);

            assertThat(price).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("종료 후에는 최저가를 반환한다")
        void afterEnd() {
            Offer offer = createOffer(DecayType.LINEAR);
            Instant after = END.plusSeconds(60);

            Money price = calculator.calculate(offer, after);

            assertThat(price).isEqualTo(Money.of(5000));
        }

        @Test
        @DisplayName("10원 단위로 내림 처리된다")
        void floorToTenWon() {
            // 정확히 10원 단위가 안 나오는 케이스
            // basePrice=10000, minPrice=5000, 감쇠구간=600초
            // 100초 경과 시: progress = 100/600 = 1/6
            // price = 10000 - 5000 * (1/6) = 10000 - 833.33... = 9166.66...
            // 10원 내림 -> 9160
            Offer offer = createOffer(DecayType.LINEAR);
            Instant at = START.plusSeconds(100);

            Money price = calculator.calculate(offer, at);

            assertThat(price).isEqualTo(Money.of(9160));
        }
    }

    @Nested
    @DisplayName("EXPONENTIAL 타입")
    class ExponentialType {

        @Test
        @DisplayName("아직 구현되지 않았다")
        void notImplemented() {
            Offer offer = createOffer(DecayType.EXPONENTIAL);

            assertThatThrownBy(() -> calculator.calculate(offer, START))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("basePrice와 minPrice가 같으면 항상 같은 가격")
        void samePrices() {
            Offer offer = new Offer(
                    Money.of(10000),
                    Money.of(10000),
                    DecayType.LINEAR,
                    START, END, EXPIRE
            );

            assertThat(calculator.calculate(offer, START)).isEqualTo(Money.of(10000));
            assertThat(calculator.calculate(offer, END)).isEqualTo(Money.of(10000));
        }
    }

    private Offer createOffer(DecayType decayType) {
        return new Offer(
                Money.of(10000),
                Money.of(5000),
                decayType,
                START, END, EXPIRE
        );
    }
}
