package com.englishlms.course.service;

import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface LessonService {

    PageResponse<LessonResponse> getLessons(int page, int size, String keyword, UUID courseId, Boolean isPublished, String sort);

    LessonResponse getLessonById(UUID id);

    LessonResponse getLessonByIdWithAuthorization(UUID id, String email, String role);

    List<LessonResponse> getLessonsByCourseId(UUID courseId);

    LessonResponse createLesson(LessonRequest request);

    LessonResponse updateLesson(UUID id, LessonRequest request);

    LessonResponse reorderLesson(UUID id, int newOrderIndex);

    LessonResponse moveUp(UUID id);

    LessonResponse moveDown(UUID id);

    void deleteLesson(UUID id);
}
