package com.tima.lms.dto.response;

import lombok.Data;

@Data
public class EnrollmentResponse {
    private Long id;
    private String enrollDate;
    private String status;
    private String userName;
    private String courseTitle;
}