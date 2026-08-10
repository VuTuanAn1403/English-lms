-- Migration V13: Performance Optimization Indexes for Course & Learning Progress Queries

CREATE INDEX IF NOT EXISTS idx_enrollments_user_course ON enrollments (user_id, course_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_email_course ON enrollments (student_email, course_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_status ON enrollments (status);

CREATE INDEX IF NOT EXISTS idx_learning_progress_user_course_lesson ON learning_progress (user_id, course_id, lesson_id);
CREATE INDEX IF NOT EXISTS idx_learning_progress_completed ON learning_progress (completed);
CREATE INDEX IF NOT EXISTS idx_learning_progress_user_lesson ON learning_progress (user_id, lesson_id);
