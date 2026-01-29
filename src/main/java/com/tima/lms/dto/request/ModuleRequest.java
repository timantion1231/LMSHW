package com.tima.lms.dto.request;

import lombok.Data;

@Data
public class ModuleRequest {
    private String title;
    private Integer orderIndex;
    private String description;
    private Long courseId;
}