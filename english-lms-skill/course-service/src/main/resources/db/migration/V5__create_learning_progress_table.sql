-- V5: Create learning_progress table and seed lesson progress records

CREATE TABLE IF NOT EXISTS learning_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    lesson_id UUID NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    last_accessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_spent INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_learning_progress_user_lesson UNIQUE (user_id, lesson_id)
);

CREATE INDEX IF NOT EXISTS idx_learning_progress_user ON learning_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_learning_progress_course ON learning_progress(course_id);
CREATE INDEX IF NOT EXISTS idx_learning_progress_user_course ON learning_progress(user_id, course_id);

-- Seed granular lesson progress for existing enrollments
INSERT INTO learning_progress (user_id, course_id, lesson_id, completed, completed_at, last_accessed_at)
SELECT 
    e.user_id, 
    e.course_id, 
    l.id AS lesson_id,
    CASE WHEN l.rn <= e.completed_lessons THEN TRUE ELSE FALSE END AS completed,
    CASE WHEN l.rn <= e.completed_lessons THEN CURRENT_TIMESTAMP - INTERVAL '1 day' * (10 - l.rn) ELSE NULL END AS completed_at,
    CURRENT_TIMESTAMP - INTERVAL '2 hours' * (10 - l.rn) AS last_accessed_at
FROM enrollments e
JOIN (
    SELECT id, course_id, ROW_NUMBER() OVER (PARTITION BY course_id ORDER BY lesson_order, id) as rn
    FROM lessons
) l ON e.course_id = l.course_id
ON CONFLICT (user_id, lesson_id) DO NOTHING;
