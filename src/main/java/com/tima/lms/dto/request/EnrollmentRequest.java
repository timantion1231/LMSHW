package com.tima.lms.dto.request;

import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long userId;
    private Long courseId;
}