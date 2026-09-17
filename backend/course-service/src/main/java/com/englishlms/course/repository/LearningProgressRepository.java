package com.englishlms.course.repository;

import com.englishlms.course.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {
    List<LearningProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);
    List<LearningProgress> findByUserId(UUID userId);
    Optional<LearningProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);
    long countByUserIdAndCourseIdAndCompletedTrue(UUID userId, UUID courseId);
    Optional<LearningProgress> findFirstByUserIdAndCourseIdOrderByLastAccessedAtDesc(UUID userId, UUID courseId);
    long countByUserIdAndCompletedTrue(UUID userId);
    void deleteByLessonId(UUID lessonId);

    @org.springframework.data.jpa.repository.Query("SELECT lp.userId, lp.course.id, COUNT(lp) FROM LearningProgress lp WHERE lp.completed = true GROUP BY lp.userId, lp.course.id")
    List<Object[]> countCompletedGroupByUserIdAndCourseId();
}
