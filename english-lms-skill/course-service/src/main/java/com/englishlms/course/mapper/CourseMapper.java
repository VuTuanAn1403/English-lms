package com.englishlms.course.mapper;

import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.Lesson;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    @Mapping(target = "effectivePrice", expression = "java(course.getEffectivePrice())")
    @Mapping(target = "isFree", expression = "java(course.isFree())")
    @Mapping(target = "totalLessons", expression = "java(course.getLessons() != null ? course.getLessons().size() : 0)")
    CourseResponse toCourseResponse(Course course);

    List<CourseResponse> toCourseResponseList(List<Course> courses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toCourse(CourseRequest request);

    @Named("toFull")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "orderIndex", source = "lessonOrder")
    @Mapping(target = "documentUrl", expression = "java(lesson.getDocumentUrl() != null ? lesson.getDocumentUrl() : lesson.getPdfUrl())")
    @Mapping(target = "pdfUrl", expression = "java(lesson.getPdfUrl() != null ? lesson.getPdfUrl() : lesson.getDocumentUrl())")
    @Mapping(target = "isTrial", expression = "java(lesson.getLessonOrder() != null && lesson.getLessonOrder() <= 5)")
    LessonResponse toLessonResponse(Lesson lesson);

    @IterableMapping(qualifiedByName = "toFull")
    List<LessonResponse> toLessonResponseList(List<Lesson> lessons);

    @Named("toSummary")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "orderIndex", source = "lessonOrder")
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "pdfUrl", ignore = true)
    @Mapping(target = "documentUrl", ignore = true)
    @Mapping(target = "isTrial", expression = "java(lesson.getLessonOrder() != null && lesson.getLessonOrder() <= 5)")
    LessonResponse toLessonSummaryResponse(Lesson lesson);

    @IterableMapping(qualifiedByName = "toSummary")
    List<LessonResponse> toLessonSummaryResponseList(List<Lesson> lessons);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "pdfUrl", expression = "java(request.getPdfUrl() != null ? request.getPdfUrl() : request.getDocumentUrl())")
    @Mapping(target = "documentUrl", expression = "java(request.getDocumentUrl() != null ? request.getDocumentUrl() : request.getPdfUrl())")
    @Mapping(target = "lessonOrder", expression = "java(request.getLessonOrder() != null ? request.getLessonOrder() : (request.getOrderIndex() != null ? request.getOrderIndex() : 1))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson toLesson(LessonRequest request);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.title")
    @Mapping(target = "level", source = "course.level")
    @Mapping(target = "thumbnail", source = "course.imageUrl")
    @Mapping(target = "course", source = "course")
    @Mapping(target = "studentName", expression = "java(enrollment.getStudentName() != null ? enrollment.getStudentName() : (enrollment.getStudentEmail() != null ? enrollment.getStudentEmail().split(\"@\")[0] : \"Học viên\"))")
    @Mapping(target = "studentEmail", expression = "java(enrollment.getStudentEmail() != null ? enrollment.getStudentEmail() : \"student@gmail.com\")")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    List<EnrollmentResponse> toEnrollmentResponseList(List<Enrollment> enrollments);
}
