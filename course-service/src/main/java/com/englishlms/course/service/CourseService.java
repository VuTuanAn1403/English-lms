package com.englishlms.course.service;

import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseById(UUID id);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(UUID id, CourseRequest request);

    void deleteCourse(UUID id);

    List<LessonResponse> getLessonsByCourseId(UUID courseId);

    LessonResponse getLessonById(UUID id);

    LessonResponse createLesson(LessonRequest request);

    EnrollmentResponse enroll(String email, EnrollmentRequest request);

    List<EnrollmentResponse> getMyEnrollments(String email);
}
