package com.spotprice.domain.offer;

import com.spotprice.domain.common.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest {

    // 테스트용 기준 시간
    private static final Instant NOW = Instant.parse("2025-01-01T12:00:00Z");
    private static final Instant START = NOW;
    private static final Instant END = NOW.plusSeconds(600);      // 10분 후
    private static final Instant EXPIRE = NOW.plusSeconds(900);   // 15분 후

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("유효한 값으로 Offer를 생성한다")
        void createValid() {
            Offer offer = new Offer(
                    Money.of(10000),
                    Money.of(5000),
                    DecayType.LINEAR,
                    START, END, EXPIRE
            );

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
            assertThat(offer.getBasePrice()).isEqualTo(Money.of(10000));
            assertThat(offer.getMinPrice()).isEqualTo(Money.of(5000));
            assertThat(offer.getDecayType()).isEqualTo(DecayType.LINEAR);
        }

        @Test
        @DisplayName("최저가가 기본가보다 크면 예외 발생")
        void minPriceGreaterThanBasePrice() {
            assertThatThrownBy(() -> new Offer(
                    Money.of(5000),
                    Money.of(10000),
                    DecayType.LINEAR,
                    START, END, EXPIRE
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("기본가는 최저가보다 커야 합니다");
        }

        @Test
        @DisplayName("시작 시간이 종료 시간보다 늦으면 예외 발생")
        void startAfterEnd() {
            assertThatThrownBy(() -> new Offer(
                    Money.of(10000),
                    Money.of(5000),
                    DecayType.LINEAR,
                    END, START, EXPIRE  // 순서 바뀜
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("시작 시간은 종료 시간보다 빨라야 합니다");
        }

        @Test
        @DisplayName("감쇠 종료 시간이 만료 시간보다 늦으면 예외 발생")
        void endAfterExpire() {
            assertThatThrownBy(() -> new Offer(
                    Money.of(10000),
                    Money.of(5000),
                    DecayType.LINEAR,
                    START, EXPIRE, END  // end > expire
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("감쇠 종료 시간은 만료 시간보다 늦을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("판매 (sell)")
    class Sell {

        @Test
        @DisplayName("OPEN 상태에서 판매 가능")
        void sellFromOpen() {
            Offer offer = createDefaultOffer();

            offer.sell(NOW.plusSeconds(60));

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.SOLD);
        }

        @Test
        @DisplayName("판매 시 OfferSoldEvent가 발행된다")
        void sellEmitsEvent() {
            Offer offer = createDefaultOffer();

            offer.sell(NOW.plusSeconds(60));

            assertThat(offer.pullEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(OfferSoldEvent.class);
        }

        @Test
        @DisplayName("이미 판매된 Offer는 다시 판매할 수 없다")
        void cannotSellTwice() {
            Offer offer = createDefaultOffer();
            offer.sell(NOW.plusSeconds(60));

            assertThatThrownBy(() -> offer.sell(NOW.plusSeconds(120)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("판매 불가능한 상태");
        }

        @Test
        @DisplayName("만료 시간 이후에는 판매할 수 없다")
        void cannotSellAfterExpire() {
            Offer offer = createDefaultOffer();

            assertThatThrownBy(() -> offer.sell(EXPIRE.plusSeconds(1)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("만료된 Offer");
        }
    }

    @Nested
    @DisplayName("만료 (expire)")
    class Expire {

        @Test
        @DisplayName("만료 시간 도달 시 EXPIRED 상태로 전이")
        void expireAtExpireTime() {
            Offer offer = createDefaultOffer();

            offer.expire(EXPIRE);

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료 시 OfferExpiredEvent가 발행된다")
        void expireEmitsEvent() {
            Offer offer = createDefaultOffer();

            offer.expire(EXPIRE);

            assertThat(offer.pullEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(OfferExpiredEvent.class);
        }

        @Test
        @DisplayName("만료 시간 전에는 만료되지 않는다")
        void notExpireBeforeTime() {
            Offer offer = createDefaultOffer();

            offer.expire(EXPIRE.minusSeconds(1));

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
        }

        @Test
        @DisplayName("이미 판매된 Offer는 만료 처리되지 않는다 (멱등)")
        void soldOfferNotExpired() {
            Offer offer = createDefaultOffer();
            offer.sell(NOW.plusSeconds(60));
            offer.pullEvents(); // 이벤트 비우기

            offer.expire(EXPIRE);

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.SOLD);
            assertThat(offer.pullEvents()).isEmpty();
        }

        @Test
        @DisplayName("이미 만료된 Offer에 다시 expire 호출해도 멱등하게 처리")
        void expireIdempotent() {
            Offer offer = createDefaultOffer();
            offer.expire(EXPIRE);
            offer.pullEvents();

            offer.expire(EXPIRE.plusSeconds(60));

            assertThat(offer.getStatus()).isEqualTo(OfferStatus.EXPIRED);
            assertThat(offer.pullEvents()).isEmpty();
        }
    }

    private Offer createDefaultOffer() {
        return new Offer(
                Money.of(10000),
                Money.of(5000),
                DecayType.LINEAR,
                START, END, EXPIRE
        );
    }
}
