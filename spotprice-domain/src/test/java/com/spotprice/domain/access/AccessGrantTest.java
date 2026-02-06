package com.spotprice.domain.access;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessGrantTest {

    private static final Instant VALID_FROM = Instant.parse("2025-01-01T14:00:00Z");
    private static final Instant VALID_TO = Instant.parse("2025-01-01T16:00:00Z");

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("유효한 값으로 AccessGrant를 생성한다")
        void createValid() {
            AccessGrant grant = new AccessGrant(1L, GrantType.PIN, "482916", VALID_FROM, VALID_TO);

            assertThat(grant.getOrderId()).isEqualTo(1L);
            assertThat(grant.getGrantType()).isEqualTo(GrantType.PIN);
            assertThat(grant.getGrantValue()).isEqualTo("482916");
            assertThat(grant.getValidFrom()).isEqualTo(VALID_FROM);
            assertThat(grant.getValidTo()).isEqualTo(VALID_TO);
            assertThat(grant.getStatus()).isEqualTo(AccessGrantStatus.ACTIVE);
        }

        @Test
        @DisplayName("orderId가 null이면 예외 발생")
        void nullOrderId() {
            assertThatThrownBy(() -> new AccessGrant(null, GrantType.PIN, "482916", VALID_FROM, VALID_TO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderId");
        }

        @Test
        @DisplayName("grantValue가 빈 문자열이면 예외 발생")
        void blankGrantValue() {
            assertThatThrownBy(() -> new AccessGrant(1L, GrantType.PIN, "  ", VALID_FROM, VALID_TO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("grantValue");
        }

        @Test
        @DisplayName("validFrom이 validTo보다 늦으면 예외 발생")
        void invalidTimeRange() {
            assertThatThrownBy(() -> new AccessGrant(1L, GrantType.PIN, "482916", VALID_TO, VALID_FROM))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("validFrom은 validTo보다 빨라야 합니다");
        }

        @Test
        @DisplayName("validFrom과 validTo가 같으면 예외 발생")
        void sameTime() {
            assertThatThrownBy(() -> new AccessGrant(1L, GrantType.PIN, "482916", VALID_FROM, VALID_FROM))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("validFrom은 validTo보다 빨라야 합니다");
        }
    }

    @Nested
    @DisplayName("취소 (revoke)")
    class Revoke {

        @Test
        @DisplayName("ACTIVE 상태에서 취소 가능")
        void revokeFromActive() {
            AccessGrant grant = createDefault();

            grant.revoke();

            assertThat(grant.getStatus()).isEqualTo(AccessGrantStatus.REVOKED);
        }

        @Test
        @DisplayName("이미 REVOKED 상태이면 취소 시 예외 발생")
        void cannotRevokeIfAlreadyRevoked() {
            AccessGrant grant = createDefault();
            grant.revoke();

            assertThatThrownBy(grant::revoke)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ACTIVE 상태에서만 취소 가능");
        }
    }

    @Nested
    @DisplayName("DB 복원")
    class Restore {

        @Test
        @DisplayName("restore로 기존 상태를 복원한다")
        void restoreFromDb() {
            AccessGrant grant = AccessGrant.restore(
                    100L, 1L, GrantType.QR, "uuid-value",
                    VALID_FROM, VALID_TO, AccessGrantStatus.ACTIVE
            );

            assertThat(grant.getId()).isEqualTo(100L);
            assertThat(grant.getGrantType()).isEqualTo(GrantType.QR);
            assertThat(grant.getStatus()).isEqualTo(AccessGrantStatus.ACTIVE);
        }
    }

    private AccessGrant createDefault() {
        return new AccessGrant(1L, GrantType.PIN, "482916", VALID_FROM, VALID_TO);
    }
}
