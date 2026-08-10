package com.englishlms.course;

import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LearningProgressRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LearningProgressRepository learningProgressRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseResponse courseResponse;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        course = Course.builder()
                .id(courseId)
                .title("English Communication")
                .description("Basic communication course")
                .level("BEGINNER")
                .build();

        courseResponse = CourseResponse.builder()
                .id(courseId)
                .title("English Communication")
                .description("Basic communication course")
                .level("BEGINNER")
                .build();
    }

    @Test
    @DisplayName("Lấy danh sách khóa học thành công")
    void getAllCourses_Success() {
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));
        when(courseMapper.toCourseResponseList(anyList())).thenReturn(Collections.singletonList(courseResponse));

        List<CourseResponse> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English Communication", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Lấy chi tiết khóa học theo ID - Thành công")
    void getCourseById_Success() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toCourseResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());
    }

    @Test
    @DisplayName("Lấy chi tiết khóa học theo ID - Không tìm thấy")
    void getCourseById_NotFound_ThrowsException() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    @DisplayName("Đăng ký khóa học thành công")
    void enroll_Success() {
        EnrollmentRequest request = EnrollmentRequest.builder().courseId(courseId).build();
        UUID userId = UUID.nameUUIDFromBytes("student@gmail.com".getBytes());
        Enrollment enrollment = Enrollment.builder().id(UUID.randomUUID()).userId(userId).course(course).build();
        EnrollmentResponse response = EnrollmentResponse.builder().id(enrollment.getId()).course(courseResponse).build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentEmailAndCourseId(eq("student@gmail.com"), eq(courseId))).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
        when(courseMapper.toEnrollmentResponse(enrollment)).thenReturn(response);

        EnrollmentResponse result = courseService.enroll("student@gmail.com", request);

        assertNotNull(result);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Đăng ký khóa học trùng lặp - Ném ngoại lệ")
    void enroll_AlreadyEnrolled_ThrowsException() {
        EnrollmentRequest request = EnrollmentRequest.builder().courseId(courseId).build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentEmailAndCourseId(eq("student@gmail.com"), eq(courseId))).thenReturn(true);

        assertThrows(AppException.class, () -> courseService.enroll("student@gmail.com", request));
    }

    @Test
    @DisplayName("Xóa khóa học - Có bài học đi kèm -> Ném lỗi 409 Conflict")
    void deleteCourse_WithLessons_ThrowsConflictException() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.countByCourseId(courseId)).thenReturn(5L);

        assertThrows(AppException.class, () -> courseService.deleteCourse(courseId));
    }

    @Test
    @DisplayName("Xóa khóa học - Có học viên đăng ký -> Ném lỗi 409 Conflict")
    void deleteCourse_WithEnrollments_ThrowsConflictException() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.countByCourseId(courseId)).thenReturn(0L);
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(3L);

        assertThrows(AppException.class, () -> courseService.deleteCourse(courseId));
    }

    @Test
    @DisplayName("Xóa khóa học rỗng - Thành công")
    void deleteCourse_Success() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.countByCourseId(courseId)).thenReturn(0L);
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(0L);

        courseService.deleteCourse(courseId);

        verify(courseRepository).delete(course);
    }
}
