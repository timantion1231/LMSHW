package com.tima.lms.dto.response;

import lombok.Data;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private String startDate;
    private String categoryName;
    private String teacherName;
}