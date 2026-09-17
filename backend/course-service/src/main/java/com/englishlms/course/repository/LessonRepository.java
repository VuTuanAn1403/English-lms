package com.englishlms.course.repository;

import com.englishlms.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {

    List<Lesson> findByCourseIdOrderByLessonOrderAsc(UUID courseId);

    List<Lesson> findByCourseIdAndLessonOrder(UUID courseId, Integer lessonOrder);

    @Query("SELECT MAX(l.lessonOrder) FROM Lesson l WHERE l.course.id = :courseId")
    Integer findMaxLessonOrderByCourseId(@Param("courseId") UUID courseId);

    boolean existsByCourseIdAndLessonOrder(UUID courseId, Integer lessonOrder);

    long countByCourseId(UUID courseId);

    @Query("SELECT l.course.id, COUNT(l) FROM Lesson l GROUP BY l.course.id")
    List<Object[]> countLessonsGroupByCourseId();
}
