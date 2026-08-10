-- V11__add_course_pricing_orders_payments.sql
-- 1. Add pricing and publication fields to courses table
ALTER TABLE courses
    ADD COLUMN IF NOT EXISTS price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS sale_price DECIMAL(12, 2) NULL,
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10) NOT NULL DEFAULT 'VND',
    ADD COLUMN IF NOT EXISTS published BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing sample courses with realistic prices (5 free courses, remaining paid)
-- Make 5 courses free (price = 0.00)
UPDATE courses SET price = 0.00, sale_price = NULL WHERE title LIKE '%Cơ bản%' OR title LIKE '%Nhập môn%' OR title LIKE '%Basic%';

-- Update remaining courses with paid prices and optional sale prices
UPDATE courses SET price = 499000.00, sale_price = 299000.00 WHERE price = 0.00 AND title NOT LIKE '%Cơ bản%' AND title NOT LIKE '%Nhập môn%' AND title NOT LIKE '%Basic%';
UPDATE courses SET price = 699000.00, sale_price = 499000.00 WHERE id IN (SELECT id FROM courses OFFSET 5 LIMIT 10);
UPDATE courses SET price = 899000.00, sale_price = 699000.00 WHERE id IN (SELECT id FROM courses OFFSET 15 LIMIT 15);
UPDATE courses SET price = 1199000.00, sale_price = 899000.00 WHERE id IN (SELECT id FROM courses OFFSET 30 LIMIT 20);

-- 2. Migrate existing enrollments status to LEGACY_FREE
UPDATE enrollments 
SET status = 'LEGACY_FREE' 
WHERE status IS NULL OR status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED');

-- 3. Create course_orders table
CREATE TABLE IF NOT EXISTS course_orders (
    id UUID PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    user_email_snapshot VARCHAR(100) NOT NULL,
    course_id UUID NOT NULL,
    course_title_snapshot VARCHAR(255) NOT NULL,
    original_price DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'VND',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_provider VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_order_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON course_orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_course_id ON course_orders(course_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON course_orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_paid_at ON course_orders(paid_at);

-- 4. Create payment_transactions table
CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_transaction_id VARCHAR(100) NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'VND',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_code VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NULL,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES course_orders(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payment_transactions(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_provider_tx_id ON payment_transactions(provider_transaction_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payment_transactions(status);
