package com.tima.lms.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String duration;
    private LocalDate startDate;
    private Long categoryId;
    private Long teacherId;
}