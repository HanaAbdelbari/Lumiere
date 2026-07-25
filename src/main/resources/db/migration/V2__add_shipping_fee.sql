-- =====================================================================
-- Lumière — V2: add shipping_fee to orders
-- PostgreSQL
--
-- Shipping is frozen at order time, exactly like unit_price, total_amount,
-- and deposit_amount. If shipping rates change later, historical orders must
-- keep the fee the customer actually paid.
--
-- shipping_fee = 0 for free shipping (orders >= EGP 800), so the check is
-- >= 0 (not > 0), mirroring deposit_amount.
--
-- total_amount now INCLUDES shipping (products + shipping). The deposit is
-- computed from that final total.
-- =====================================================================

ALTER TABLE orders
    ADD COLUMN shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0;

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_shipping_non_negative CHECK (shipping_fee >= 0);
