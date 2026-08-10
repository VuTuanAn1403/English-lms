package com.englishlms.course.service;

import com.englishlms.course.dto.AdminEnrollmentStatsResponse;
import com.englishlms.course.dto.CourseAccessResponse;
import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.dto.UpdateProgressRequest;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    PageResponse<CourseResponse> getCourses(int page, int size, String keyword, String level, String category, String sort);

    CourseResponse getCourseById(UUID id);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(UUID id, CourseRequest request);

    void deleteCourse(UUID id);

    List<LessonResponse> getLessonsByCourseId(UUID courseId);

    LessonResponse getLessonById(UUID id);

    LessonResponse createLesson(LessonRequest request);

    EnrollmentResponse enroll(String email, EnrollmentRequest request);

    EnrollmentResponse enroll(String email, UUID courseId);

    CourseAccessResponse getCourseAccess(UUID courseId, String email);

    EnrollmentResponse startTrial(UUID courseId, String email, String studentName);

    void unenroll(String email, UUID courseId);

    List<EnrollmentResponse> getMyEnrollments(String email);

    EnrollmentResponse getEnrollmentByCourse(String email, UUID courseId);

    EnrollmentResponse updateProgress(String email, UUID courseId, UpdateProgressRequest request);

    List<EnrollmentResponse> getAllEnrollmentsForAdmin(String search, String status);

    AdminEnrollmentStatsResponse getAdminEnrollmentStatistics();

    long getEnrolledCountByCourse(UUID courseId);
}
