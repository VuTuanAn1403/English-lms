package com.englishlms.course.service.impl;

import com.englishlms.course.dto.AdminProgressAnalyticsResponse;
import com.englishlms.course.dto.CourseProgressResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonProgressDto;
import com.englishlms.course.dto.ProgressRequest;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.EnrollmentStatus;
import com.englishlms.course.entity.LearningProgress;
import com.englishlms.course.entity.Lesson;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LearningProgressRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.service.CourseService;
import com.englishlms.course.service.ProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgressServiceImpl implements ProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public ProgressServiceImpl(
            LearningProgressRepository learningProgressRepository,
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            CourseService courseService,
            CourseMapper courseMapper
    ) {
        this.learningProgressRepository = learningProgressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseProgressResponse completeLesson(String email, ProgressRequest request) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + request.getCourseId()));
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với id: " + request.getLessonId()));

        if (!lesson.getCourse().getId().equals(course.getId())) {
            throw new com.englishlms.course.exception.AppException(
                    "Bài học [" + request.getLessonId() + "] không thuộc khóa học [" + course.getTitle() + "].",
                    org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }

        String studentName = email.contains("@") ? email.split("@")[0] : email;

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, course.getId())
                .orElseGet(() -> enrollmentRepository.save(Enrollment.builder()
                        .userId(userId)
                        .studentEmail(email)
                        .studentName(studentName)
                        .course(course)
                        .completedLessons(0)
                        .totalLessons((int) lessonRepository.countByCourseId(course.getId()))
                        .progress(0)
                        .completed(false)
                        .status(EnrollmentStatus.NOT_STARTED)
                        .enrolledAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));

        if (enrollment.getStudentEmail() == null) {
            enrollment.setStudentEmail(email);
            enrollment.setStudentName(studentName);
        }

        LocalDateTime now = LocalDateTime.now();
        Optional<LearningProgress> lpOpt = learningProgressRepository.findByUserIdAndLessonId(userId, lesson.getId());
        LearningProgress lp;
        if (lpOpt.isPresent()) {
            lp = lpOpt.get();
            lp.setCompleted(true);
            if (lp.getCompletedAt() == null) {
                lp.setCompletedAt(now);
            }
            lp.setLastAccessedAt(now);
        } else {
            lp = LearningProgress.builder()
                    .userId(userId)
                    .course(course)
                    .lesson(lesson)
                    .completed(true)
                    .completedAt(now)
                    .lastAccessedAt(now)
                    .build();
        }
        learningProgressRepository.save(lp);

        updateEnrollmentProgressFromRecords(userId, course, enrollment);

        return getCourseProgress(email, course.getId());
    }

    @Override
    public CourseProgressResponse uncompleteLesson(String email, ProgressRequest request) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + request.getCourseId()));

        Optional<LearningProgress> lpOpt = learningProgressRepository.findByUserIdAndLessonId(userId, request.getLessonId());
        if (lpOpt.isPresent()) {
            LearningProgress lp = lpOpt.get();
            lp.setCompleted(false);
            lp.setCompletedAt(null);
            lp.setLastAccessedAt(LocalDateTime.now());
            learningProgressRepository.save(lp);
        }

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, course.getId()).orElse(null);
        if (enrollment != null) {
            updateEnrollmentProgressFromRecords(userId, course, enrollment);
        }

        return getCourseProgress(email, course.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(String email, UUID courseId) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + courseId));

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        List<LearningProgress> userProgressList = learningProgressRepository.findByUserIdAndCourseId(userId, courseId);
        Map<UUID, LearningProgress> progressMap = userProgressList.stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p, (a, b) -> a));

        List<LessonProgressDto> lessonDtos = new ArrayList<>();
        UUID nextLessonId = null;
        int completedCount = 0;

        for (Lesson l : lessons) {
            LearningProgress lp = progressMap.get(l.getId());
            boolean isCompleted = lp != null && Boolean.TRUE.equals(lp.getCompleted());
            if (isCompleted) {
                completedCount++;
            } else if (nextLessonId == null) {
                nextLessonId = l.getId();
            }

            lessonDtos.add(LessonProgressDto.builder()
                    .lessonId(l.getId())
                    .lessonTitle(l.getTitle())
                    .lessonOrder(l.getLessonOrder())
                    .completed(isCompleted)
                    .completedAt(lp != null ? lp.getCompletedAt() : null)
                    .lastAccessedAt(lp != null ? lp.getLastAccessedAt() : null)
                    .build());
        }

        int total = lessons.size();
        int pct = total > 0 ? (completedCount * 100) / total : 0;
        boolean isAllCompleted = total > 0 && pct >= 100;
        String status = isAllCompleted ? "COMPLETED" : (pct > 0 ? "IN_PROGRESS" : "NOT_STARTED");

        return CourseProgressResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .completedLessons(completedCount)
                .totalLessons(total)
                .progressPercent(pct)
                .completed(isAllCompleted)
                .status(status)
                .nextLessonId(nextLessonId != null ? nextLessonId : (lessons.isEmpty() ? null : lessons.get(0).getId()))
                .lessons(lessonDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseProgressResponse> getMyProgressList(String email) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        return enrollments.stream()
                .map(e -> getCourseProgress(email, e.getCourse().getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAdminProgressList(String search, String status) {
        return courseService.getAllEnrollmentsForAdmin(search, status);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProgressAnalyticsResponse getAdminProgressAnalytics() {
        long totalStudySessions = learningProgressRepository.count();
        long completedCoursesCount = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        long inProgressCoursesCount = enrollmentRepository.countByStatus(EnrollmentStatus.IN_PROGRESS);
        long notStartedCoursesCount = enrollmentRepository.countByStatus(EnrollmentStatus.NOT_STARTED);
        long totalEnrollments = enrollmentRepository.count();

        double completionRate = totalEnrollments > 0 ? (completedCoursesCount * 100.0) / totalEnrollments : 0.0;
        completionRate = Math.round(completionRate * 100.0) / 100.0;

        List<Enrollment> enrollments = enrollmentRepository.findAll();

        long range0to25 = enrollments.stream().filter(e -> (e.getProgress() != null ? e.getProgress() : 0) <= 25).count();
        long range25to50 = enrollments.stream().filter(e -> e.getProgress() != null && e.getProgress() > 25 && e.getProgress() <= 50).count();
        long range50to75 = enrollments.stream().filter(e -> e.getProgress() != null && e.getProgress() > 50 && e.getProgress() <= 75).count();
        long range75to100 = enrollments.stream().filter(e -> e.getProgress() != null && e.getProgress() > 75).count();

        // Top Students from real learning_progress database
        List<LearningProgress> allProgress = learningProgressRepository.findAll();
        Map<UUID, Long> studentLessonsMap = allProgress.stream()
                .filter(p -> Boolean.TRUE.equals(p.getCompleted()))
                .collect(Collectors.groupingBy(LearningProgress::getUserId, Collectors.counting()));

        List<AdminProgressAnalyticsResponse.TopStudentDto> topStudents = studentLessonsMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    UUID uId = entry.getKey();
                    Enrollment userEnr = enrollments.stream().filter(e -> e.getUserId().equals(uId)).findFirst().orElse(null);
                    String email = userEnr != null && userEnr.getStudentEmail() != null ? userEnr.getStudentEmail() : "student@gmail.com";
                    String name = userEnr != null && userEnr.getStudentName() != null ? userEnr.getStudentName() : (email.contains("@") ? email.split("@")[0] : email);
                    long courseCount = enrollments.stream().filter(e -> e.getUserId().equals(uId)).count();
                    return AdminProgressAnalyticsResponse.TopStudentDto.builder()
                            .userId(uId)
                            .studentEmail(email)
                            .studentName(name)
                            .completedLessonsCount(entry.getValue())
                            .enrolledCoursesCount(courseCount)
                            .build();
                }).collect(Collectors.toList());

        // Top Courses by real completion count
        Map<UUID, Long> courseCompletedMap = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .collect(Collectors.groupingBy(e -> e.getCourse().getId(), Collectors.counting()));

        List<AdminProgressAnalyticsResponse.TopCourseDto> topCourses = courseCompletedMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    UUID cId = entry.getKey();
                    Course c = courseRepository.findById(cId).orElse(null);
                    long totalEnr = enrollmentRepository.countByCourseId(cId);
                    return AdminProgressAnalyticsResponse.TopCourseDto.builder()
                            .courseId(cId)
                            .courseTitle(c != null ? c.getTitle() : "Khóa học Tiếng Anh")
                            .level(c != null ? c.getLevel() : "BEGINNER")
                            .completedCount(entry.getValue())
                            .totalEnrollments(totalEnr)
                            .build();
                }).collect(Collectors.toList());

        return AdminProgressAnalyticsResponse.builder()
                .totalStudySessions(totalStudySessions)
                .completedCoursesCount(completedCoursesCount)
                .inProgressCoursesCount(inProgressCoursesCount)
                .notStartedCoursesCount(notStartedCoursesCount)
                .completionRate(completionRate)
                .progressDistribution(AdminProgressAnalyticsResponse.ProgressDistributionDto.builder()
                        .range0to25(range0to25)
                        .range25to50(range25to50)
                        .range50to75(range50to75)
                        .range75to100(range75to100)
                        .build())
                .topStudents(topStudents)
                .topCourses(topCourses)
                .build();
    }

    private void updateEnrollmentProgressFromRecords(UUID userId, Course course, Enrollment enrollment) {
        long completedCount = learningProgressRepository.countByUserIdAndCourseIdAndCompletedTrue(userId, course.getId());
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        int totalLessons = lessons.size();

        int pct = totalLessons > 0 ? Math.min(100, (int) ((completedCount * 100) / totalLessons)) : 0;
        enrollment.setCompletedLessons((int) completedCount);
        enrollment.setTotalLessons(totalLessons);
        enrollment.setProgress(pct);

        if (pct >= 100 && totalLessons > 0) {
            enrollment.setProgress(100);
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompleted(true);
            if (enrollment.getCompletionDate() == null) {
                enrollment.setCompletionDate(LocalDateTime.now());
            }
        } else if (pct > 0) {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
            enrollment.setCompleted(false);
        } else {
            enrollment.setStatus(EnrollmentStatus.NOT_STARTED);
            enrollment.setCompleted(false);
        }
        enrollmentRepository.save(enrollment);
    }
}
