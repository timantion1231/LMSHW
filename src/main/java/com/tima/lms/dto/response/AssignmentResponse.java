package com.tima.lms.dto.response;

import lombok.Data;

@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private String dueDate;
    private Integer maxScore;
    private String lessonTitle;
}