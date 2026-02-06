INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('OPEN', 'LINEAR', 100000, 50000,
        TIMESTAMP '2026-02-06 00:00:00', TIMESTAMP '2026-02-07 00:00:00', TIMESTAMP '2026-02-08 00:00:00');

INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('SOLD', 'LINEAR', 80000, 40000,
        TIMESTAMP '2026-02-01 00:00:00', TIMESTAMP '2026-02-02 00:00:00', TIMESTAMP '2026-02-03 00:00:00');

INSERT INTO offers (status, decay_type, base_price, min_price, start_at, end_at, expire_at)
VALUES ('EXPIRED', 'NONE', 60000, 60000,
        TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-02 00:00:00', TIMESTAMP '2026-01-03 00:00:00');
