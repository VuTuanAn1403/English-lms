package com.englishlms.course.service.impl;

import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.Lesson;
import com.englishlms.course.exception.CoursePurchaseRequiredException;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LearningProgressRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.service.LessonService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final CourseMapper courseMapper;

    public LessonServiceImpl(
            LessonRepository lessonRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            LearningProgressRepository learningProgressRepository,
            CourseMapper courseMapper
    ) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.learningProgressRepository = learningProgressRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LessonResponse> getLessons(int page, int size, String keyword, UUID courseId, Boolean isPublished, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.ASC, "lessonOrder");
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

        Specification<Lesson> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String k = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), k);
                Predicate contentLike = cb.like(cb.lower(root.get("content")), k);
                Predicate descLike = cb.like(cb.lower(root.get("description")), k);
                predicates.add(cb.or(titleLike, contentLike, descLike));
            }

            if (courseId != null) {
                predicates.add(cb.equal(root.get("course").get("id"), courseId));
            }

            if (isPublished != null) {
                predicates.add(cb.equal(root.get("isPublished"), isPublished));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Lesson> lessonPage = lessonRepository.findAll(spec, pageable);

        List<LessonResponse> items = lessonPage.getContent().stream()
                .map(courseMapper::toLessonResponse)
                .collect(Collectors.toList());

        return PageResponse.<LessonResponse>builder()
                .items(items)
                .pageNumber(lessonPage.getNumber())
                .pageSize(lessonPage.getSize())
                .totalElements(lessonPage.getTotalElements())
                .totalPages(lessonPage.getTotalPages())
                .isLast(lessonPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));
        return courseMapper.toLessonResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonByIdWithAuthorization(UUID id, String email, String role) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));

        Course course = lesson.getCourse();

        // 1. ADMIN has full access to all lessons
        if (role != null && (role.contains("ADMIN") || role.contains("ROLE_ADMIN"))) {
            return courseMapper.toLessonResponse(lesson);
        }

        // 2. Free course (effectivePrice == 0) -> Access granted
        if (course.isFree()) {
            return courseMapper.toLessonResponse(lesson);
        }

        // 3. Paid course: check lesson order
        List<Lesson> sortedLessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        int lessonIndex = -1;
        for (int i = 0; i < sortedLessons.size(); i++) {
            if (sortedLessons.get(i).getId().equals(lesson.getId())) {
                lessonIndex = i;
                break;
            }
        }

        // First 5 lessons (index 0..4) are trial lessons
        boolean isTrialLesson = (lessonIndex >= 0 && lessonIndex < 5);

        if (isTrialLesson) {
            // Trial lesson -> allowed for logged in student
            if (email == null || email.isBlank()) {
                throw new CoursePurchaseRequiredException("Vui lòng đăng nhập để học thử 5 bài đầu tiên.");
            }
            return courseMapper.toLessonResponse(lesson);
        }

        // 4. Lesson 6+ (index 5+) requires active or legacy enrollment
        if (email == null || email.isBlank()) {
            throw new CoursePurchaseRequiredException("Bài học này yêu cầu thanh toán để xem tiếp. Vui lòng mua khóa học.");
        }

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentEmailAndCourseId(email, course.getId());
        if (enrollmentOpt.isPresent() && enrollmentOpt.get().getStatus().hasFullAccess()) {
            return courseMapper.toLessonResponse(lesson);
        }

        throw new CoursePurchaseRequiredException(
                "Khóa học yêu cầu thanh toán để học từ bài 6 trở đi. Vui lòng mua khóa học " + course.getTitle() + ".");
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourseId(UUID courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        return courseMapper.toLessonSummaryResponseList(lessons);
    }

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + request.getCourseId()));

        Lesson lesson = courseMapper.toLesson(request);
        lesson.setCourse(course);

        if (request.getLessonOrder() == null && request.getOrderIndex() == null) {
            Integer maxOrder = lessonRepository.findMaxLessonOrderByCourseId(course.getId());
            lesson.setLessonOrder(maxOrder != null ? maxOrder + 1 : 1);
        }

        Lesson saved = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(saved);
    }

    @Override
    public LessonResponse updateLesson(UUID id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getDescription() != null) lesson.setDescription(request.getDescription());
        if (request.getContent() != null) lesson.setContent(request.getContent());
        if (request.getVideoUrl() != null) lesson.setVideoUrl(request.getVideoUrl());
        if (request.getPdfUrl() != null) {
            lesson.setPdfUrl(request.getPdfUrl());
            lesson.setDocumentUrl(request.getPdfUrl());
        }
        if (request.getDocumentUrl() != null) {
            lesson.setDocumentUrl(request.getDocumentUrl());
            if (lesson.getPdfUrl() == null) lesson.setPdfUrl(request.getDocumentUrl());
        }
        if (request.getDuration() != null) lesson.setDuration(request.getDuration());
        if (request.getLessonOrder() != null) lesson.setLessonOrder(request.getLessonOrder());
        else if (request.getOrderIndex() != null) lesson.setLessonOrder(request.getOrderIndex());

        if (request.getIsPublished() != null) lesson.setIsPublished(request.getIsPublished());

        Lesson updated = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(updated);
    }

    @Override
    public LessonResponse reorderLesson(UUID id, int newOrderIndex) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));
        lesson.setLessonOrder(newOrderIndex);
        Lesson updated = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(updated);
    }

    @Override
    public LessonResponse moveUp(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));
        int currentOrder = lesson.getLessonOrder();
        if (currentOrder > 1) {
            lesson.setLessonOrder(currentOrder - 1);
            lessonRepository.save(lesson);
        }
        return courseMapper.toLessonResponse(lesson);
    }

    @Override
    public LessonResponse moveDown(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));
        lesson.setLessonOrder(lesson.getLessonOrder() + 1);
        Lesson updated = lessonRepository.save(lesson);
        return courseMapper.toLessonResponse(updated);
    }

    @Override
    public void deleteLesson(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + id));
        learningProgressRepository.deleteByLessonId(id);
        lessonRepository.delete(lesson);
    }
}
