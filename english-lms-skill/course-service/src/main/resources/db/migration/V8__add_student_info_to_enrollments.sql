-- V8: Add student_email and student_name columns to enrollments table
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS student_email VARCHAR(255);
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS student_name VARCHAR(255);

-- Update existing enrollments data with real student info
UPDATE enrollments 
SET student_email = 'student@gmail.com',
    student_name = 'Nguyễn Văn A'
WHERE student_email IS NULL;
