package com.tima.lms.dto.response;

import lombok.Data;

@Data
public class SubmissionResponse {
    private Long id;
    private String submittedAt;
    private String content;
    private Integer score;
    private String feedback;
    private String assignmentTitle;
    private String studentName;
}