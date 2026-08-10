package com.englishlms.course.service;

import com.englishlms.course.dto.AdminProgressAnalyticsResponse;
import com.englishlms.course.dto.CourseProgressResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.ProgressRequest;

import java.util.List;
import java.util.UUID;

public interface ProgressService {

    CourseProgressResponse completeLesson(String email, ProgressRequest request);

    CourseProgressResponse uncompleteLesson(String email, ProgressRequest request);

    CourseProgressResponse getCourseProgress(String email, UUID courseId);

    List<CourseProgressResponse> getMyProgressList(String email);

    List<EnrollmentResponse> getAdminProgressList(String search, String status);

    AdminProgressAnalyticsResponse getAdminProgressAnalytics();
}
