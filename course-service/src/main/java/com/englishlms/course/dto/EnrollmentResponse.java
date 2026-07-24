package com.englishlms.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private UUID id;
    private UUID userId;
    private CourseResponse course;
    private Integer progress;
    private Boolean completed;
    private LocalDateTime enrolledAt;
}
