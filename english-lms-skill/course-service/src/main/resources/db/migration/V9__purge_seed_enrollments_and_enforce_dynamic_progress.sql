-- V9: Purge old synthetic seed enrollments from V4 and enforce dynamic progress calculation
-- 1. Remove all old seed enrollments that have no actual completed learning_progress records
DELETE FROM enrollments 
WHERE user_id = '017f9ea8-4ee4-3197-9d44-58509d67386a'::uuid
  AND NOT EXISTS (
      SELECT 1 FROM learning_progress lp 
      WHERE lp.user_id = enrollments.user_id 
        AND lp.course_id = enrollments.course_id 
        AND lp.completed = true
  );

-- 2. Ensure Unique Constraint on (user_id, course_id) exists in PostgreSQL
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_enrollments_user_course') THEN
        ALTER TABLE enrollments ADD CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id);
    END IF;
END $$;

-- 3. Reset progress for remaining enrollments to match actual learning_progress records
UPDATE enrollments e
SET completed_lessons = COALESCE((
        SELECT COUNT(*) FROM learning_progress lp 
        WHERE lp.user_id = e.user_id AND lp.course_id = e.course_id AND lp.completed = true
    ), 0),
    total_lessons = COALESCE((
        SELECT COUNT(*) FROM lessons l WHERE l.course_id = e.course_id
    ), 10);

-- Recalculate progress % and status based on real completed_lessons vs total_lessons
UPDATE enrollments
SET progress = CASE WHEN total_lessons > 0 THEN (completed_lessons * 100) / total_lessons ELSE 0 END,
    status = CASE 
        WHEN total_lessons > 0 AND completed_lessons >= total_lessons THEN 'COMPLETED'
        WHEN completed_lessons > 0 THEN 'IN_PROGRESS'
        ELSE 'NOT_STARTED'
    END,
    completed = CASE WHEN total_lessons > 0 AND completed_lessons >= total_lessons THEN true ELSE false END;
