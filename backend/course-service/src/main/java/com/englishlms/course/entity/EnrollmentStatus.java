package com.englishlms.course.entity;

public enum EnrollmentStatus {
    TRIAL,
    ACTIVE,
    LEGACY_FREE,
    CANCELLED,
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED;

    public boolean hasFullAccess() {
        return this == ACTIVE || this == LEGACY_FREE || this == IN_PROGRESS || this == COMPLETED;
    }
}
