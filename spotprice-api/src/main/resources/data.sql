-- OPEN Offers (미래 날짜)
INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('OPEN', 'LINEAR', 100000, 50000,
        TIMESTAMP '2026-02-12 00:00:00', TIMESTAMP '2026-02-14 00:00:00', TIMESTAMP '2026-02-15 00:00:00');

INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('OPEN', 'LINEAR', 80000, 30000,
        TIMESTAMP '2026-02-12 00:00:00', TIMESTAMP '2026-02-16 00:00:00', TIMESTAMP '2026-02-17 00:00:00');

INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('OPEN', 'LINEAR', 150000, 70000,
        TIMESTAMP '2026-02-11 00:00:00', TIMESTAMP '2026-02-13 00:00:00', TIMESTAMP '2026-02-14 00:00:00');

-- SOLD
INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('SOLD', 'LINEAR', 80000, 40000,
        TIMESTAMP '2026-02-01 00:00:00', TIMESTAMP '2026-02-02 00:00:00', TIMESTAMP '2026-02-03 00:00:00');

-- EXPIRED
INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('EXPIRED', 'NONE', 60000, 60000,
        TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-02 00:00:00', TIMESTAMP '2026-01-03 00:00:00');
