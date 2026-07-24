package com.englishlms.course.service.impl;

import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.Lesson;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            EnrollmentRepository enrollmentRepository,
            CourseMapper courseMapper
    ) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toCourseResponseList(courseRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + id));
        return courseMapper.toCourseResponse(course);
    }

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Course course = courseMapper.toCourse(request);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getImageUrl() != null) course.setImageUrl(request.getImageUrl());

        Course updated = courseRepository.save(course);
        return courseMapper.toCourseResponse(updated);
    }

    @Override
    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourseId(UUID courseId) {
        return courseMapper.toLessonResponseList(
                lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với id: " + id));
        return courseMapper.toLessonResponse(lesson);
    }

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + request.getCourseId()));

        Lesson lesson = courseMapper.toLesson(request);
        lesson.setCourse(course);
        Lesson savedLesson = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(savedLesson);
    }

    @Override
    public EnrollmentResponse enroll(String email, EnrollmentRequest request) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + request.getCourseId()));

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, request.getCourseId())) {
            throw new AppException("Bạn đã đăng ký khóa học này trước đó!", HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .course(course)
                .progress(0)
                .completed(false)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return courseMapper.toEnrollmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String email) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return courseMapper.toEnrollmentResponseList(enrollments);
    }
}
