-- Migration V12: Normalize Course Levels & Media URLs
UPDATE courses 
SET level = 'BEGINNER' 
WHERE level IS NULL OR level IN ('Cơ bản', 'Cơ Bản', 'BASIC', 'Elementary', 'Beginner', 'beginner');

UPDATE courses 
SET level = 'INTERMEDIATE' 
WHERE level IN ('Trung cấp', 'Trung Cấp', 'Intermediate', 'intermediate');

UPDATE courses 
SET level = 'ADVANCED' 
WHERE level IN ('Nâng cao', 'Nâng Cao', 'Advanced', 'advanced');

-- Clean up fake example.com media URLs
UPDATE lessons 
SET pdf_url = NULL 
WHERE pdf_url LIKE '%example.com%';

UPDATE lessons 
SET document_url = NULL 
WHERE document_url LIKE '%example.com%';

UPDATE lessons 
SET video_url = NULL 
WHERE video_url LIKE '%example.com%';
