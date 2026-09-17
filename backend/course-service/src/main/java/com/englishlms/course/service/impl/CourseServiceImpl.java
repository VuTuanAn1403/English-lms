package com.englishlms.course.service.impl;

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
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.EnrollmentStatus;
import com.englishlms.course.entity.Lesson;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.exception.CoursePurchaseRequiredException;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LearningProgressRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.service.CourseService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            EnrollmentRepository enrollmentRepository,
            LearningProgressRepository learningProgressRepository,
            CourseMapper courseMapper
    ) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.learningProgressRepository = learningProgressRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByCreatedAtDesc();
        return courseMapper.toCourseResponseList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getCourses(int page, int size, String keyword, String level, String category, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                Sort.Direction dir = "desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
                sortObj = Sort.by(dir, parts[0]);
            } else {
                sortObj = Sort.by(Sort.Direction.ASC, parts[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String k = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), k);
                Predicate descLike = cb.like(cb.lower(root.get("description")), k);
                predicates.add(cb.or(titleLike, descLike));
            }

            if (level != null && !level.isBlank() && !"ALL".equalsIgnoreCase(level)) {
                predicates.add(cb.equal(cb.upper(root.get("level")), level.trim().toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);

        List<CourseResponse> items = coursePage.getContent().stream()
                .map(courseMapper::toCourseResponse)
                .collect(Collectors.toList());

        return PageResponse.<CourseResponse>builder()
                .items(items)
                .pageNumber(coursePage.getNumber())
                .pageSize(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .isLast(coursePage.isLast())
                .build();
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
        Course saved = courseRepository.save(course);
        return courseMapper.toCourseResponse(saved);
    }

    @Override
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + id));

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getImageUrl() != null) course.setImageUrl(request.getImageUrl());

        Course updated = courseRepository.save(course);
        return courseMapper.toCourseResponse(updated);
    }

    @Override
    public void deleteCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + id));

        long lessonCount = lessonRepository.countByCourseId(id);
        if (lessonCount > 0) {
            throw new com.englishlms.course.exception.AppException(
                    "Không thể xóa khóa học đã chứa " + lessonCount + " bài học. Vui lòng xóa các bài học thuộc khóa học trước.",
                    org.springframework.http.HttpStatus.CONFLICT
            );
        }

        long enrollmentCount = enrollmentRepository.countByCourseId(id);
        if (enrollmentCount > 0) {
            throw new com.englishlms.course.exception.AppException(
                    "Không thể xóa khóa học đã có " + enrollmentCount + " học viên đăng ký.",
                    org.springframework.http.HttpStatus.CONFLICT
            );
        }

        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourseId(UUID courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        return courseMapper.toLessonResponseList(lessons);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với id: " + lessonId));
        return courseMapper.toLessonResponse(lesson);
    }

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + request.getCourseId()));

        Lesson lesson = courseMapper.toLesson(request);
        lesson.setCourse(course);

        Lesson saved = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseAccessResponse getCourseAccess(UUID courseId, String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        if (email == null || email.isBlank()) {
            return CourseAccessResponse.builder()
                    .authenticated(false)
                    .enrollmentStatus(null)
                    .canStartTrial(false)
                    .trialLessonLimit(5)
                    .completedTrialLessons(0)
                    .hasFullAccess(course.isFree())
                    .purchaseRequired(!course.isFree())
                    .effectivePrice(course.getEffectivePrice())
                    .currency(course.getCurrency())
                    .build();
        }

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentEmailAndCourseId(email, courseId);
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            boolean hasFullAccess = enrollment.getStatus().hasFullAccess() || course.isFree();
            int completedTrial = Math.min(enrollment.getCompletedLessons(), 5);

            return CourseAccessResponse.builder()
                    .authenticated(true)
                    .enrollmentStatus(enrollment.getStatus())
                    .canStartTrial(!hasFullAccess && enrollment.getStatus() != EnrollmentStatus.TRIAL)
                    .trialLessonLimit(5)
                    .completedTrialLessons(completedTrial)
                    .hasFullAccess(hasFullAccess)
                    .purchaseRequired(!hasFullAccess)
                    .effectivePrice(course.getEffectivePrice())
                    .currency(course.getCurrency())
                    .build();
        }

        return CourseAccessResponse.builder()
                .authenticated(true)
                .enrollmentStatus(null)
                .canStartTrial(!course.isFree())
                .trialLessonLimit(5)
                .completedTrialLessons(0)
                .hasFullAccess(course.isFree())
                .purchaseRequired(!course.isFree())
                .effectivePrice(course.getEffectivePrice())
                .currency(course.getCurrency())
                .build();
    }

    @Override
    public EnrollmentResponse startTrial(UUID courseId, String email, String name) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentEmailAndCourseId(email, courseId);
        if (existingOpt.isPresent()) {
            return enrichEnrollmentResponseWithDynamicProgress(existingOpt.get());
        }

        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        String sName = (name != null && !name.isBlank()) ? name : (email.contains("@") ? email.split("@")[0] : email);
        int totalLessonsCount = (int) lessonRepository.countByCourseId(courseId);

        EnrollmentStatus initialStatus = course.isFree() ? EnrollmentStatus.ACTIVE : EnrollmentStatus.TRIAL;

        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .studentEmail(email)
                .studentName(sName)
                .course(course)
                .progress(0)
                .completedLessons(0)
                .totalLessons(totalLessonsCount)
                .completed(false)
                .status(initialStatus)
                .enrolledAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrichEnrollmentResponseWithDynamicProgress(saved);
    }

    @Override
    public EnrollmentResponse enroll(String email, EnrollmentRequest request) {
        return enroll(email, request.getCourseId());
    }

    @Override
    public EnrollmentResponse enroll(String email, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với id: " + courseId));

        if (!course.isFree()) {
            throw new CoursePurchaseRequiredException("Khóa học này là khóa học trả phí. Vui lòng chọn mua khóa học để đăng ký.");
        }

        if (enrollmentRepository.existsByStudentEmailAndCourseId(email, courseId)) {
            throw new AppException("Học viên đã đăng ký khóa học này rồi.", HttpStatus.CONFLICT);
        }

        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        int totalLessonsCount = (int) lessonRepository.countByCourseId(courseId);
        String studentName = email.contains("@") ? email.split("@")[0] : email;

        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .studentEmail(email)
                .studentName(studentName)
                .course(course)
                .progress(0)
                .completedLessons(0)
                .totalLessons(totalLessonsCount)
                .completed(false)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrichEnrollmentResponseWithDynamicProgress(saved);
    }

    @Override
    public void unenroll(String email, UUID courseId) {
        if (!enrollmentRepository.existsByStudentEmailAndCourseId(email, courseId)) {
            throw new ResourceNotFoundException("Chưa tìm thấy đăng ký cho khóa học này!");
        }
        enrollmentRepository.deleteByStudentEmailAndCourseId(email, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String email) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentEmailOrderByEnrolledAtDesc(email);
        return enrollments.stream().map(e -> {
            EnrollmentResponse resp = enrichEnrollmentResponseWithDynamicProgress(e);
            if (resp.getStudentEmail() == null) resp.setStudentEmail(email);
            if (resp.getStudentName() == null) resp.setStudentName(email.split("@")[0]);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentByCourse(String email, UUID courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentEmailAndCourseId(email, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa tìm thấy thông tin đăng ký cho khóa học này."));
        EnrollmentResponse resp = enrichEnrollmentResponseWithDynamicProgress(enrollment);
        if (resp.getStudentEmail() == null) resp.setStudentEmail(email);
        if (resp.getStudentName() == null) resp.setStudentName(email.split("@")[0]);
        return resp;
    }

    @Override
    public EnrollmentResponse updateProgress(String email, UUID courseId, UpdateProgressRequest request) {
        Enrollment enrollment = enrollmentRepository.findByStudentEmailAndCourseId(email, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Học viên chưa đăng ký khóa học này!"));

        int total = (int) lessonRepository.countByCourseId(courseId);

        if (request.getCompletedLessons() != null) {
            enrollment.setCompletedLessons(total > 0 ? Math.min(request.getCompletedLessons(), total) : 0);
            int pct = total > 0 ? (enrollment.getCompletedLessons() * 100) / total : 0;
            enrollment.setProgress(pct);
        } else if (request.getProgress() != null) {
            int pct = Math.min(Math.max(request.getProgress(), 0), 100);
            enrollment.setProgress(pct);
            enrollment.setCompletedLessons(total > 0 ? (pct * total) / 100 : 0);
        } else {
            enrollment.setCompletedLessons(total > 0 ? Math.min(enrollment.getCompletedLessons() + 1, total) : 0);
            int pct = total > 0 ? (enrollment.getCompletedLessons() * 100) / total : 0;
            enrollment.setProgress(pct);
        }

        if (enrollment.getProgress() == 0 || total == 0) {
            if (enrollment.getStatus() != EnrollmentStatus.TRIAL) {
                enrollment.setStatus(EnrollmentStatus.NOT_STARTED);
            }
            enrollment.setCompleted(false);
        } else if (enrollment.getProgress() >= 100) {
            enrollment.setProgress(100);
            enrollment.setCompletedLessons(total);
            if (enrollment.getStatus() != EnrollmentStatus.TRIAL) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
            }
            enrollment.setCompleted(true);
            if (enrollment.getCompletionDate() == null) {
                enrollment.setCompletionDate(LocalDateTime.now());
            }
        } else {
            if (enrollment.getStatus() != EnrollmentStatus.TRIAL) {
                enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
            }
            enrollment.setCompleted(false);
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrichEnrollmentResponseWithDynamicProgress(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollmentsForAdmin(String search, String status) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByOrderByEnrolledAtDesc();
        Map<UUID, Long> lessonCounts = getLessonCountMap();
        Map<String, Long> completedCounts = getCompletedProgressMap();

        return enrollments.stream()
                .map(e -> enrichEnrollmentFast(e, lessonCounts, completedCounts))
                .filter(r -> {
                    if (r == null) return false;
                    boolean matchesSearch = true;
                    if (search != null && !search.isBlank()) {
                        String q = search.toLowerCase().trim();
                        matchesSearch = (r.getCourseName() != null && r.getCourseName().toLowerCase().contains(q)) ||
                                        (r.getStudentEmail() != null && r.getStudentEmail().toLowerCase().contains(q)) ||
                                        (r.getStudentName() != null && r.getStudentName().toLowerCase().contains(q));
                    }
                    boolean matchesStatus = true;
                    if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                        matchesStatus = r.getStatus() != null && status.equalsIgnoreCase(r.getStatus().name());
                    }
                    return matchesSearch && matchesStatus;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminEnrollmentStatsResponse getAdminEnrollmentStatistics() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        Map<UUID, Long> lessonCounts = getLessonCountMap();
        Map<String, Long> completedCounts = getCompletedProgressMap();

        long total = enrollments.size();
        long inProgress = 0;
        long completed = 0;
        long notStarted = 0;

        for (Enrollment e : enrollments) {
            EnrollmentResponse enriched = enrichEnrollmentFast(e, lessonCounts, completedCounts);
            if (enriched != null) {
                if (enriched.getStatus() == EnrollmentStatus.COMPLETED) {
                    completed++;
                } else if (enriched.getStatus() == EnrollmentStatus.IN_PROGRESS) {
                    inProgress++;
                } else {
                    notStarted++;
                }
            }
        }

        double rate = total > 0 ? (completed * 100.0) / total : 0.0;
        rate = Math.round(rate * 100.0) / 100.0;

        long totalCoursesCount = courseRepository.count();
        long totalLessonsCount = lessonRepository.count();

        return AdminEnrollmentStatsResponse.builder()
                .totalEnrollments(total)
                .inProgressCount(inProgress)
                .completedCount(completed)
                .notStartedCount(notStarted)
                .completionRate(rate)
                .totalCourses(totalCoursesCount)
                .totalLessons(totalLessonsCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long getEnrolledCountByCourse(UUID courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    private Map<UUID, Long> getLessonCountMap() {
        Map<UUID, Long> map = new HashMap<>();
        try {
            List<Object[]> rows = lessonRepository.countLessonsGroupByCourseId();
            if (rows != null) {
                for (Object[] r : rows) {
                    if (r != null && r.length >= 2 && r[0] != null && r[1] != null) {
                        map.put((UUID) r[0], ((Number) r[1]).longValue());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to pre-aggregate lesson counts: {}", ex.getMessage());
        }
        return map;
    }

    private Map<String, Long> getCompletedProgressMap() {
        Map<String, Long> map = new HashMap<>();
        try {
            List<Object[]> rows = learningProgressRepository.countCompletedGroupByUserIdAndCourseId();
            if (rows != null) {
                for (Object[] r : rows) {
                    if (r != null && r.length >= 3 && r[0] != null && r[1] != null && r[2] != null) {
                        String key = r[0].toString() + "_" + r[1].toString();
                        map.put(key, ((Number) r[2]).longValue());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to pre-aggregate completed learning progress: {}", ex.getMessage());
        }
        return map;
    }

    private EnrollmentResponse enrichEnrollmentFast(Enrollment e, Map<UUID, Long> lessonCountMap, Map<String, Long> completedMap) {
        if (e == null || e.getCourse() == null) return null;
        EnrollmentResponse resp = courseMapper.toEnrollmentResponse(e);
        if (resp == null) return null;

        UUID courseId = e.getCourse().getId();
        long totalLessons = (courseId != null && lessonCountMap != null) ? lessonCountMap.getOrDefault(courseId, 0L) : 0L;
        String key = (e.getUserId() != null && courseId != null) ? (e.getUserId().toString() + "_" + courseId.toString()) : "";
        long realCompleted = (completedMap != null) ? completedMap.getOrDefault(key, 0L) : 0L;

        int pct = totalLessons > 0 ? (int) ((realCompleted * 100) / totalLessons) : 0;
        if (pct > 100) pct = 100;

        EnrollmentStatus status = e.getStatus();
        if (status == EnrollmentStatus.ACTIVE || status == EnrollmentStatus.LEGACY_FREE || status == EnrollmentStatus.NOT_STARTED || status == EnrollmentStatus.IN_PROGRESS || status == EnrollmentStatus.COMPLETED) {
            status = (pct >= 100 && totalLessons > 0) ? EnrollmentStatus.COMPLETED : ((pct > 0) ? EnrollmentStatus.IN_PROGRESS : (e.getStatus() == EnrollmentStatus.LEGACY_FREE ? EnrollmentStatus.LEGACY_FREE : EnrollmentStatus.NOT_STARTED));
        }

        resp.setCompletedLessons((int) realCompleted);
        resp.setTotalLessons((int) totalLessons);
        resp.setProgress(pct);
        resp.setStatus(status);
        resp.setCompleted(pct >= 100 && totalLessons > 0);

        return resp;
    }

    private EnrollmentResponse enrichEnrollmentResponseWithDynamicProgress(Enrollment e) {
        if (e == null || e.getCourse() == null) return null;
        EnrollmentResponse resp = courseMapper.toEnrollmentResponse(e);
        if (resp == null) return null;

        long realCompleted = learningProgressRepository != null ? learningProgressRepository.countByUserIdAndCourseIdAndCompletedTrue(e.getUserId(), e.getCourse().getId()) : 0;
        long totalLessons = lessonRepository != null ? lessonRepository.countByCourseId(e.getCourse().getId()) : 0;

        int pct = totalLessons > 0 ? (int) ((realCompleted * 100) / totalLessons) : 0;
        if (pct > 100) pct = 100;

        EnrollmentStatus status = e.getStatus();
        if (status == EnrollmentStatus.ACTIVE || status == EnrollmentStatus.LEGACY_FREE || status == EnrollmentStatus.NOT_STARTED || status == EnrollmentStatus.IN_PROGRESS || status == EnrollmentStatus.COMPLETED) {
            status = (pct >= 100 && totalLessons > 0) ? EnrollmentStatus.COMPLETED : ((pct > 0) ? EnrollmentStatus.IN_PROGRESS : (e.getStatus() == EnrollmentStatus.LEGACY_FREE ? EnrollmentStatus.LEGACY_FREE : EnrollmentStatus.NOT_STARTED));
        }

        resp.setCompletedLessons((int) realCompleted);
        resp.setTotalLessons((int) totalLessons);
        resp.setProgress(pct);
        resp.setStatus(status);
        resp.setCompleted(pct >= 100 && totalLessons > 0);

        return resp;
    }
}
