-- V3: Add status column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Update existing users status to ACTIVE
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;
