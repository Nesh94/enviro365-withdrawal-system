-- Sample investors: a mix of ages so the retirement-age rule (>65) can be
-- exercised both ways during testing/demo.
INSERT INTO investors (id, first_name, last_name, email, date_of_birth) VALUES
  (1, 'John', 'Smith', 'john.smith@example.com', '1955-03-14'),   -- age > 65
  (2, 'Sarah', 'Johnson', 'sarah.johnson@example.com', '1990-07-22'), -- age < 65
  (3, 'Michael', 'Ndlovu', 'michael.ndlovu@example.com', '1958-11-02'), -- age > 65
  (4, 'Amahle', 'Dlamini', 'amahle.dlamini@example.com', '1975-01-30'); -- age < 65

INSERT INTO products (id, investor_id, product_name, product_type, balance) VALUES
  (1, 1, 'Retirement Annuity',   'RETIREMENT_ANNUITY', 850000.00),
  (2, 1, 'Flexible Unit Trust',  'UNIT_TRUST',          120000.00),
  (3, 2, 'Flexible Unit Trust',  'UNIT_TRUST',           45000.00),
  (4, 3, 'Retirement Annuity',   'RETIREMENT_ANNUITY', 610000.00),
  (5, 4, 'Money Market Fund',    'MONEY_MARKET',         32000.00);

-- A couple of historical withdrawals so the history table / CSV export
-- have something to show on first run.
INSERT INTO withdrawal_notices (id, product_id, investor_id, amount, balance_after, withdrawal_type, status, request_date) VALUES
  (1, 1, 1, 50000.00, 850000.00, 'RETIREMENT', 'APPROVED', '2026-06-15 09:30:00'),
  (2, 3, 2, 5000.00,  45000.00,  'STANDARD',   'APPROVED', '2026-07-02 14:05:00');

-- balance_after on each seeded notice matches the product's current
-- seeded balance, since these were the most recent withdrawal against
-- that product. Any new withdrawal submitted via the API deducts from
-- the live balance from that point onward.

-- H2's IDENTITY columns don't know about the explicit ids used above —
-- without this, the next auto-generated id would start back at 1 and
-- collide with the seed data (primary key violation) the first time a
-- withdrawal is submitted through the app.
ALTER TABLE investors ALTER COLUMN id RESTART WITH 5;
ALTER TABLE products ALTER COLUMN id RESTART WITH 6;
ALTER TABLE withdrawal_notices ALTER COLUMN id RESTART WITH 3;
