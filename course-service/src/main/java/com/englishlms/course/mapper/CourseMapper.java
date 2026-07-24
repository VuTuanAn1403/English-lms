package com.englishlms.course.mapper;

import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    CourseResponse toCourseResponse(Course course);

    List<CourseResponse> toCourseResponseList(List<Course> courses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toCourse(CourseRequest request);

    @Mapping(target = "courseId", source = "course.id")
    LessonResponse toLessonResponse(Lesson lesson);

    List<LessonResponse> toLessonResponseList(List<Lesson> lessons);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Lesson toLesson(LessonRequest request);

    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    List<EnrollmentResponse> toEnrollmentResponseList(List<Enrollment> enrollments);
}
