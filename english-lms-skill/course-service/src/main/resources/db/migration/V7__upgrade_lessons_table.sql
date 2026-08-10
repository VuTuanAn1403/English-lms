-- V7: Upgrade lessons table schema with description, document_url, duration, is_published, updated_at
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS document_url VARCHAR(500);
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS duration INT DEFAULT 15;
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS is_published BOOLEAN DEFAULT true;
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Sync data for existing rows
UPDATE lessons SET document_url = pdf_url WHERE document_url IS NULL AND pdf_url IS NOT NULL;
UPDATE lessons SET duration = 15 WHERE duration IS NULL;
UPDATE lessons SET is_published = true WHERE is_published IS NULL;
UPDATE lessons SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;
