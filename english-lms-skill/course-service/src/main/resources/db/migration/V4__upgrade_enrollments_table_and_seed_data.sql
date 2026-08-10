-- V4: Upgrade enrollments table and seed test data for 10 students across 50 courses

ALTER TABLE enrollments 
ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
ADD COLUMN IF NOT EXISTS completed_lessons INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS total_lessons INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS completion_date TIMESTAMP;

-- Add Unique Constraint to prevent duplicate enrollments
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_enrollments_user_course') THEN
        ALTER TABLE enrollments ADD CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id);
    END IF;
END $$;

-- Create index for status query performance
CREATE INDEX IF NOT EXISTS idx_enrollments_status ON enrollments(status);

-- Clear old basic enrollments if any to re-seed cleanly
TRUNCATE TABLE enrollments;

-- Seed 50 enrollments for 10 students using subqueries on existing 50 courses
-- Student 1: student@gmail.com (UUID: 017f9ea8-4ee4-3197-9d44-58509d67386a)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at, completion_date)
SELECT '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid, id, 100, 10, 10, TRUE, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn = 1
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid, id, 50, 5, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn = 2
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid, id, 30, 3, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn = 3
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid, id, 0, 0, 10, FALSE, 'NOT_STARTED', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn = 4
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid, id, 80, 8, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '3 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn = 5
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 2 (UUID: 22222222-2222-2222-2222-222222222222)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at, completion_date)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, id, 100, 10, 10, TRUE, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (6, 7, 8)
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, id, 50, 5, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (9, 10)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 3 (UUID: 33333333-3333-3333-3333-333333333333)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '33333333-3333-3333-3333-333333333333'::uuid, id, 30, 3, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '4 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (11, 12, 13, 14, 15)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 4 (UUID: 44444444-4444-4444-4444-444444444444)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '44444444-4444-4444-4444-444444444444'::uuid, id, 80, 8, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (16, 17, 18, 19, 20)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 5 (UUID: 55555555-5555-5555-5555-555555555555)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at, completion_date)
SELECT '55555555-5555-5555-5555-555555555555'::uuid, id, 100, 10, 10, TRUE, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '8 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (21, 22, 23)
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '55555555-5555-5555-5555-555555555555'::uuid, id, 10, 1, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (24, 25)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 6 (UUID: 66666666-6666-6666-6666-666666666666)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '66666666-6666-6666-6666-666666666666'::uuid, id, 0, 0, 10, FALSE, 'NOT_STARTED', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (26, 27, 28, 29, 30)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 7 (UUID: 77777777-7777-7777-7777-777777777777)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '77777777-7777-7777-7777-777777777777'::uuid, id, 50, 5, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '6 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (31, 32, 33, 34, 35)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 8 (UUID: 88888888-8888-8888-8888-888888888888)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at, completion_date)
SELECT '88888888-8888-8888-8888-888888888888'::uuid, id, 100, 10, 10, TRUE, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '12 days'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (36, 37, 38)
ON CONFLICT (user_id, course_id) DO NOTHING;

INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '88888888-8888-8888-8888-888888888888'::uuid, id, 30, 3, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '12 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (39, 40)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 9 (UUID: 99999999-9999-9999-9999-999999999999)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT '99999999-9999-9999-9999-999999999999'::uuid, id, 80, 8, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (41, 42, 43, 44, 45)
ON CONFLICT (user_id, course_id) DO NOTHING;

-- Student 10 (UUID: a1b2c3d4-e5f6-7890-abcd-ef1234567890)
INSERT INTO enrollments (user_id, course_id, progress, completed_lessons, total_lessons, completed, status, enrolled_at, updated_at)
SELECT 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid, id, 10, 1, 10, FALSE, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '5 hours'
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM courses) t WHERE rn IN (46, 47, 48, 49, 50)
ON CONFLICT (user_id, course_id) DO NOTHING;
