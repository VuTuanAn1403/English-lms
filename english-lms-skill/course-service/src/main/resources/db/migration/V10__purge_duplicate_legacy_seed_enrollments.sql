-- Migration V10: Purge duplicate legacy seed enrollments with mismatched user_ids
DELETE FROM enrollments 
WHERE student_email = 'student@gmail.com' 
  AND user_id != '22e41215-a30b-3773-a304-5f03d6ba8e89';
