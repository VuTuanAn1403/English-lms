package com.englishlms.course.repository;

import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {
    List<Enrollment> findByUserId(UUID userId);
    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(UUID userId);
    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
    void deleteByUserIdAndCourseId(UUID userId, UUID courseId);

    List<Enrollment> findByStudentEmailOrderByEnrolledAtDesc(String studentEmail);
    Optional<Enrollment> findByStudentEmailAndCourseId(String studentEmail, UUID courseId);
    boolean existsByStudentEmailAndCourseId(String studentEmail, UUID courseId);
    void deleteByStudentEmailAndCourseId(String studentEmail, UUID courseId);

    long countByCourseId(UUID courseId);
    long countByStatus(EnrollmentStatus status);
    List<Enrollment> findAllByOrderByEnrolledAtDesc();
}
